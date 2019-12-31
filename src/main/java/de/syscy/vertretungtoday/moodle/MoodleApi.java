package de.syscy.vertretungtoday.moodle;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

// Ich konnte keine gute Dokumentation für eine Moodle API finden
// (Und es könnte sein dass diese bei der Moodle Instanz des FWG sogar nicht aktiviert ist)
// deswegen habe ich mit dem Browser alle Anfragen die beim Login passieren genauer angeschaut
// und das Dateien herunterladen dann einfach, indem ich die Website scrape, gebaut.
// ---
// In dieser Klasse sind also alle Hilfsmittel um mit dem FWG Moodle zu interagieren
@Component
public class MoodleApi {
	public static final int INFORMATION_COURSE_ID = 24;
	private static final Logger LOGGER = LoggerFactory.getLogger(MoodleApi.class);
	private static final String BASE_URL = "https://lms.bildung-rp.de/fwg-trier";
	private static final String LOGIN_URL = BASE_URL + "/login/index.php";
	private static final String COURSE_CONTENT_LIST_URL = BASE_URL + "/course/view.php?id=%d";
	private static final String COURSE_FILE_LIST_URL = BASE_URL + "/course/view.php?id=%d&section=%d";
	private static final String RESOURCE_PAGE_URL = BASE_URL + "/mod/resource/view.php?id=%d";

	private String apiUser;
	private String apiPassword;

	private OkHttpClient cachedClient;

	public MoodleApi(@Value("${moodle.apiUser}") String apiUser, @Value("${moodle.apiPassword}") String apiPassword) {
		this.apiUser = apiUser;
		this.apiPassword = apiPassword;
	}

	// Die nächsten paar Methoden sind zum scrapen der Website um die Dateien (Vertretungsplan HTML Dateien und sonstiges)
	// aus dem "Informationen für Schüler Kurs" zu finden, aufzulisten und herunterzuladen.
	// Die Vorgehensweise ist ein triviales
	// * Seite laden
	// * Die richtigen Links die mich weiterführen finden
	// * Links aufrufen und von vorne anfangen im Unterordner
	// bis ich alle Resourcen habe..
	public List<Integer> getSectionIds(int courseId) throws IOException, MoodleApiException {
		OkHttpClient client = createClient();

		String url = String.format(COURSE_CONTENT_LIST_URL, courseId);
		Request request = new Request.Builder().url(url).build();

		try(Response response = client.newCall(request).execute()) {
			Document doc = Jsoup.parse(response.body().string(), url);

			List<Integer> sectionIdList = new ArrayList<>();

			for(Element link : doc.select("li > a[href]")) {
				String anchorUrl = link.attr("href");

				//Is valid course section url?
				if(anchorUrl.contains("course/view.php?id=") && anchorUrl.contains("section=")) {
					int sectionId = Integer.parseInt(anchorUrl.substring(anchorUrl.indexOf("section=") + 8));
					sectionIdList.add(sectionId);
				}
			}

			return sectionIdList;
		}
	}

	public List<Integer> getResourceIds(int courseId, int sectionId) throws IOException, MoodleApiException {
		OkHttpClient client = createClient();

		String url = String.format(COURSE_FILE_LIST_URL, courseId, sectionId);
		Request request = new Request.Builder().url(url).build();

		try(Response response = client.newCall(request).execute()) {
			Document doc = Jsoup.parse(response.body().string(), url);

			List<Integer> resourceIdList = new ArrayList<>();

			for(Element link : doc.select("div.activityinstance > a[href]")) {
				String anchorUrl = link.attr("href");

				//Example resource url: https://lms.bildung-rp.de/fwg-trier/mod/resource/view.php?id=2650
				if(anchorUrl.contains("mod/resource") && anchorUrl.contains("id=")) {
					int resourceId = Integer.parseInt(anchorUrl.substring(anchorUrl.indexOf("id=") + 3));
					resourceIdList.add(resourceId);
				}
			}

			return resourceIdList;
		}
	}

	public MoodleResourceInfo getResourceInfo(int resourceId) throws IOException, MoodleApiException {
		OkHttpClient client = createClient();

		String url = String.format(RESOURCE_PAGE_URL, resourceId);
		Request request = new Request.Builder().url(url).build();

		try(Response response = client.newCall(request).execute()) {
			if(response.code() == 303) {
				return new MoodleResourceInfo(resourceId, MoodleResourceInfo.ResourceType.FILE, response.header("Location"), "", "");
			} else {
				Document doc = Jsoup.parse(response.body().string(), url);

				Element resourceIframe = doc.selectFirst("#resourceobject");

				if(resourceIframe == null) {
					throw new MoodleApiException("Not implemented resource type (resource id: " + resourceId + ")");
				}

				String iframeSrc = resourceIframe.attr("src");

				//Not sure if needed but seems cleaner to remove the embed parameter
				if(iframeSrc.endsWith("?embed=1")) {
					iframeSrc = iframeSrc.substring(0, iframeSrc.length() - 8);
				}

				return new MoodleResourceInfo(resourceId, MoodleResourceInfo.ResourceType.EMBEDDED_PAGE, iframeSrc, "", "");
			}
		}
	}

	public List<MoodleResourceInfo> getAllResources(int courseId) throws IOException, MoodleApiException {
		List<MoodleResourceInfo> resourceInfoList = new ArrayList<>();

		List<Integer> sectionIds = getSectionIds(courseId);

		for(int sectionId : sectionIds) {
			List<Integer> resourceIds = getResourceIds(courseId, sectionId);

			for(int resourceId : resourceIds) {
				resourceInfoList.add(getResourceInfo(resourceId));
			}
		}

		return resourceInfoList;
	}

	public Response fetchResource(MoodleResourceInfo resourceInfo) throws IOException, MoodleApiException {
		OkHttpClient client = createClient();

		Request request = new Request.Builder().url(resourceInfo.getUrl()).build();

		Response response = client.newCall(request).execute();

		if(response.code() >= 400) {
			throw new MoodleApiException("Error response code " + response.code() + " when fetching resource " + resourceInfo);
		}

		return response;
	}

	// Diese Methoden sind um die "Moodle Account Bestätigung" bereitzustellen.
	// Einfach nur die Variante mit Mail schicken wäre zu langsam, deswegen dass hier.
	// Meine Logik war dass jeder eh schon einen Moodle Account hat und ich musste eh etwas machen, damit nur Leute,
	// die auch wirklich Schüler des FWGs sind, Zugriff auf die Seite haben, damit ich keinen Ärger bekomme ':D
	// -----
	// Das hier funktioniert einfach, indem ich die HTTP Anfrage, die geschickt wird, wenn man die Login Form auf der Startseite abgeschickt, imitiere.
	// Ich habe mit den Entwicklertools im Browser herausgefunden dass, je nachdem ob das Einloggen erfolgreich war,
	// ich eine andere Antwort bekomme:
	// * Bei einem erfolgreichen Login ist es ein HTTP Redirect Status Code mit einem Location Header der eine URL auf
	//   eine Seite mit einem testsession Parameter enthält
	// * Wenn der Login fehlgeschlagen ist, ist es iwie nur die normale Loginseite.
	// Mir ziemlich egal was testsession ist usw., ich brauche nur den Unterschied bei der Antwort vom Server
	// wenn der Benutzername und Passwort stimmten oder halt nicht.
	public boolean isAccountValid(String username, String password) throws IOException {
		OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();

		Request request = createLoginRequest(client, username, password);

		Response response = client.newCall(request).execute();

		return isValidLoginResponse(response);
	}

	private boolean isValidLoginResponse(Response response) {
		//Should be similar to https://lms.bildung-rp.de/fwg-trier/login/index.php?testsession=128 for valid credentials
		return response.header("Location").toLowerCase().contains("testsession");
	}

	//Not sure if a valid logintoken is required, might need to be aquired from the hidden input on the login page
	private Request createLoginRequest(OkHttpClient client, String username, String password) {
		MediaType mediaType = MediaType.parse("multipart/form-data; boundary=--------------------------544509410836185558677816");
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("username", username)
													  .addFormDataPart("password", password).addFormDataPart("rememberusername", "0")
													  .addFormDataPart("logintoken", "7GTbRs772i5zshEuSjVIlmBz4qNi6wUB").build();
		Request request = new Request.Builder().url(LOGIN_URL).method("POST", body)
											   .addHeader("Content-Type", "multipart/form-data; boundary=--------------------------544509410836185558677816")
											   .build();

		return request;
	}

	// OkHttp benutze ich als Bibliothek für alle HTTP Anfragen an externe Server, also Moodle.
	// Mit dieser Methode erstelle ich eine neue Instanz und konfiguriere diese oder gebe eine existierende zurück um nicht für jede Anfrage
	// einen neuen Client zu erstellen.. wäre schon unnötig
	private OkHttpClient createClient() throws IOException, MoodleApiException {
		if(cachedClient != null) {
			return cachedClient;
		}

		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

		// followRedirects(false) ist wichtig, damit ich den Location header beim Testen eines Accounts auslesen kann
		cachedClient = new OkHttpClient.Builder().followRedirects(false).cookieJar(new JavaNetCookieJar(cookieManager)).build();

		// Und hier logge ich mich mit einem hinterlegten Account (meinem eigenen in diesem Fall, bitte nicht sperren! :o)
		// ein um Zugriff auf die Dateien zu haben..
		Request loginRequest = createLoginRequest(cachedClient, apiUser, apiPassword);

		try(Response response = cachedClient.newCall(loginRequest).execute()) {
			if(!isValidLoginResponse(response)) {
				throw new MoodleApiException("Login with api user account failed!");
			}
		}

		return cachedClient;
	}
}