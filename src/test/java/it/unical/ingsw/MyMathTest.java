package it.unical.ingsw;

import it.unical.ingsw.connectivity.ConnectionMonitor;
import it.unical.ingsw.http.ApiClient;
import it.unical.ingsw.http.OKHttpApiClientImpl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class MyMathTest {

    private static MyMath myMath;

    // Initializes the @Mock before each @Test
    //@Rule
    //public MockitoRule mockitoRule = MockitoJUnit.rule();

    // connectionManagerMock and httpClientMock will be initialized by Mockito using
    // mock instances once MockitoRule is run
    @Mock
    private ConnectionMonitor connectionManagerMock;
    @Mock
    private ApiClient httpClientMock;

    //JUNIT 4 - The ExpectedException rule allows you to verify that your code throws a specific exception.
    // Check the Rule documentation here:
    // https://junit.org/junit4/javadoc/4.12/org/junit/Rule.html
    //@Rule
    //public ExpectedException expectedEx = ExpectedException.none();
    //@Rule public TestName name = new TestName();

    @BeforeAll
    public static void prepareAll() {
        System.out.println("before class");
    }

    @AfterAll
    public static void afterClass() {
        System.out.println("after class");
    }

    @BeforeEach
    public void prepareTest() {
        System.out.println("before");
        myMath = new MyMath(connectionManagerMock, httpClientMock);
        System.out.println(myMath);
    }

    @AfterEach
    public void cleanTest() {
        System.out.println("after");
    }

    @Test
    public void assertEpsilon(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        assertEquals(12.01, 12.05, 0.40);
    }

//	JUNIT 4: @Test(expected = IllegalArgumentException.class)
//	public void fibonacciThrowsException() {
//		System.out.println("fibonacciThrowsException");
//		myMath.fibonacci(-1);
//	}

    @Test
    public void fibonacciWorks() {
        System.out.println("testing that fibonacci works");
        assertEquals(0, myMath.fibonacci(0));
        assertEquals(1, myMath.fibonacci(1));
        assertEquals(5, myMath.fibonacci(5));
        assertEquals(8, myMath.fibonacci(6));
        assertEquals(102334155, myMath.fibonacci(40));
    }

    @Test
    public void factorialWorks() {
        System.out.println("testing that factorial works");
        assertEquals(1, myMath.factorial(0));
        assertEquals(120, myMath.factorial(5));
    }

    //@Test(timeout = 5000) - JUNIT 4
    @Test()
    @Timeout(5000)
    public void fibonacciIsFastEnough() {
        System.out.println("fibonacciIsFastEnough");
        myMath.fibonacci(40);
    }

    /**
     * Should call connectionManagerMock.isNetworkConnected(),
     * httpClientMock.post(), and return the number in the last position of the
     * RESULT array which is the nth number in the Fibonacci series
     */
    @Test
    public void remoteFibonacciWorks() throws MalformedURLException, Exception {
        System.out.println("testing that remoteFibonacci Works");
        // Inizializziamo il Map di parametri usati per la richiesta online (limit=5)
        // 5 è solo una scelta, potete usare qualsiasi numero, visto che post()
        // ritornerà quello che vogliamo noi
        int n = 5;
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("limit", String.valueOf(n));

        // Qui forziamo al metodo connectionManagerMock.isNetworkConnected() a ritornare
        // true quando venga eseguito, evitando un exception per problemi di
        // collegamento a internet
        when(connectionManagerMock.isConnected()).thenReturn(true);

        // Qui forziamo il metodo httpClientMock.sendPost() a restituire una stringa con
        // dei risultati:
        // "{\"result\":[\"0\",\"1\",\"1\",\"2\",\"3\",\"5\",\"8\",\"13\",\"21\",\"34\"]}"
        // anyString, any(String.class) e anyMap(), ci servono a indicare che il
        // comportamento che stiamo forzando verrà imposto al metodo quando esso venga
        // eseguito con qualsiasi set di parametri, ovvero sempre in questo caso
        when(httpClientMock.post(anyString(), any(String.class), anyMap()))
                .thenReturn("{\"result\":[\"0\",\"1\",\"1\",\"2\",\"3\",\"5\",\"8\",\"13\",\"21\",\"34\"]}");

        // Eseguiamo il metodo remoteFibonacci
        BigInteger fib = myMath.remoteFibonacci(n);

        // Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
        // eseguito una volta, non più non meno
        verify(connectionManagerMock, times(1)).isConnected();

        // Qui verifichiamo che httpClientMock.post() sia stato eseguito una volta
        // con i parametri giusti (post("fibonacci-numbers", "", queryParameters);)
        // Se remoteFibonacci viene eseguito con "n=5", allora post() deve
        // essere eseguito passando limit=5
        verify(httpClientMock, times(1)).post("fibonacci-numbers", "", queryParameters);

        // Verifichiamo che la risposta sia quella giusta, ovvero 34, il quale è
        // l'ultimo numero dell'array. Lo stesso array che richiediamo al nostro oggetto
        // mockato
        // (httpClientMock) di restituirci quando viene eseguito post() con
        // qualsiasi set di parametri
        assertEquals(BigInteger.valueOf(34), fib);
    }

    /**
     * Should call connectionManagerMock.isNetworkConnected(),
     * httpClientMock.post() that returns an empty string (no HTTP 200 status
     * code), and then return -1
     */
    @Test
    public void remoteFibonacciWorkWithEmptyStringResponse() throws MalformedURLException, Exception {
        System.out.println("testing that remoteFibonacci returns -1 when empty string");
        // Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
        int n = 5;
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("limit", String.valueOf(n));

        // Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a ritornare
        // true quando venga eseguito, evitando una exception per problemi di
        // collegamento a internet
        when(connectionManagerMock.isConnected()).thenReturn(true);

        // Qui forziamo il metodo httpClientMock.post() a restituire la stringa
        // "{\"result\":[]}", ovvero array di risultati vuoto
        // anyString, any(String.class) e anyMap(), ci serve ad indicare che il
        // comportamento che stiamo forzando verrà imposto al metodo quando esso venga
        // eseguito con qualsiasi set di parametri, ovvero sempre
        when(httpClientMock.post(anyString(), any(String.class), anyMap())).thenReturn("");

        // Eseguiamo il metodo che stiamo testando
        BigInteger fib = myMath.remoteFibonacci(n);

        // Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
        // eseguito una volta, non più non meno
        verify(connectionManagerMock, times(1)).isConnected();

        // Qui verifichiamo che httpClientMock.post() sia stato eseguito una volta
        // con i parametri giusti (post("fibonacci-numbers", "", queryParameters);)
        // Se remoteFibonacci viene eseguito con "n=5", allora post() deve
        // essere eseguito passando limit=5
        verify(httpClientMock, times(1)).post("fibonacci-numbers", "", queryParameters);

        // Verifichiamo che la risposta sia -1, ovvero la risposta che ci aspettiamo in caso di
        // array di risultati vuoti da parte di post()
        assertEquals(BigInteger.valueOf(-1), fib);
    }

    /**
     * Should call connectionManagerMock.isNetworkConnected(),
     * httpClientMock.post() that returns an empty array, and then return -1
     */
    @Test
    public void remoteFibonacciWorkWithEmptyResults() throws MalformedURLException, Exception {
        System.out.println("testing that remoteFibonacci returns -1 when empty results array");
        // Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
        int n = 5;
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("limit", String.valueOf(n));

        // Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
        // true quando venga eseguito, evitando una exception per collegamento a
        // internet
        when(connectionManagerMock.isConnected()).thenReturn(true);

        // Qui forziamo il metodo httpClientMock.post() a restituirci la stringa
        // "{\"result\":[]}", ovvero array di risultati vuoto
        // anyString, any(String.class) e anyMap(), ci serve a indicare che il
        // comportamento che stiamo forzando verrà imposto al metodo quando esso venga
        // eseguito con qualsiasi set di parametri, ovvero sempre
        when(httpClientMock.post(anyString(), any(String.class), anyMap())).thenReturn("{\"result\":[]}");

        // Eseguiamo il metodo che stiamo testando
        BigInteger fib = myMath.remoteFibonacci(n);

        // Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
        // eseguito una volta, non più non meno
        verify(connectionManagerMock, times(1)).isConnected();

        // Qui verifichiamo che httpClientMock.post() sia stato eseguito una volta
        // con i parametri giusti (post("fibonacci-numbers", "", queryParameters);)
        // Se remoteFibonacci viene eseguito con "n=5", allora post() deve
        // essere eseguito passando limit=5
        verify(httpClientMock, times(1)).post("fibonacci-numbers", "", queryParameters);

        // Verifichiamo che la risposta sia -1, ovvero la risposta che ci aspettiamo in caso di
        // array di risultati vuoti da parte di post()
        assertEquals(BigInteger.valueOf(-1), fib);
    }

    /**
     * Should call connectionManagerMock.isNetworkConnected(), Should call
     * httpClientMock.post() that throws IOException due to a certain problem
     */
    @Test
    public void shouldThrowIOExceptionWhenHttpRequestProblem() throws Exception {
        System.out.println("testing that remoteFibonacci throws IOException when http request problem");
        // Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
        int n = 5;
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("limit", String.valueOf(n));

        // Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
        // true quando venga eseguito, evitando una exception per collegamento a internet
        when(connectionManagerMock.isConnected()).thenReturn(true);

        // Qui forziamo il metodo httpClientMock.post() a lanciare IOException
        // anyString, any(String.class) e anyMap(), ci serve a indicare che il
        // comportamento che stiamo forzando verrà imposto al metodo quando esso venga
        // eseguito con qualsiasi set di parametri, ovvero sempre
        when(httpClientMock.post(anyString(), any(String.class), anyMap())).thenThrow(IOException.class);


        //JUNIT 4 - expectedEx.expect(IOException.class);
        // Qui controlliamo che remoteFibonacci() lancia IOException
        assertThrows(IOException.class, () -> {
            // Eseguiamo il metodo remoteFibonacci()
            myMath.remoteFibonacci(n);
        });
    }

    /**
     * Should call connectionManagerMock.isNetworkConnected(), Should call
     * httpClientMock.post() that returns a string not in JSON format, and then
     * throw JSONException
     */
    @Test
    public void shouldThrowJSONException() throws Exception {
        System.out.println("testing that remoteFibonacci throws JSONException");
        // Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
        int n = 5;
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("limit", String.valueOf(n));

        // Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
        // true quando venga eseguito, evitando un exception per il collegamento a
        // internet
        when(connectionManagerMock.isConnected()).thenReturn(true);

        // Qui forziamo il metodo httpClientMock.post() a lanciare IOException
        // anyString, any(String.class) e anyMap(), ci serve a indicare che il
        // comportamento che stiamo forzando verrà imposto al metodo quando esso venga
        // eseguito con qualsiasi set di parametri, ovvero sempre
        when(httpClientMock.post(anyString(), any(String.class), anyMap())).thenReturn("not a JSON");

        //JUNIT 4 - expectedEx.expect(JSONException.class);
        //Qui controlliamo che remoteFibonacci() lancia JSONException
        assertThrows(JSONException.class, () -> {
            myMath.remoteFibonacci(n);
        });

    }

    /**
     * Should throw IllegalArgumentException when the parameter is less than 1
     *
     * @throws IOException
     */
    @Test()
    public void remoteFibonacciThrowsExceptionWhenWrongParameter() throws Exception {
        System.out.println("testing that remoteFibonacci throws IllegalArgumentException");
        //JUNIT 4 - expectedEx.expect(RuntimeException.class);
        //Qui controlliamo che remoteFibonacci() lancia IllegalArgumentException con valori minori uguale a 0
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            myMath.remoteFibonacci(-1);
        });
        
        //Qui controlliamo che la Exception lanciata abbia il messaggio che ci aspettiamo
        assertEquals("n index must be greater than 0", ex.getMessage());
        verifyNoInteractions(httpClientMock);
        verifyNoInteractions(connectionManagerMock);
    }

    /**
     * Should throw RuntimeException when there is no Internet connection
     *
     * @throws IOException
     */
    @Test()
    public void remoteFibonacciThrowsExceptionWhenNotConnection() throws Exception {
        System.out.println("testing that remoteFibonacci throws RuntimeException when no connection");
        // Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
        // false quando venga eseguito
        when(connectionManagerMock.isConnected()).thenReturn(false);

        // Qui controlliamo che remoteFibonacci() lancia RuntimeException
        //JUNIT 4 - expectedEx.expect(RuntimeException.class);
        Exception ex = assertThrows(RuntimeException.class, () -> {
            myMath.remoteFibonacci(5);
        });

        // Qui ci aspettiamo che il messaggio della RuntimeException sia "There is not internet connection"
        //JUNIT 4 - expectedEx.expectMessage("There is not internet connection");
        assertEquals("There is not internet connection", ex.getMessage());
    }

    @Test
    public void assertStatic() throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody formBody = RequestBody.create("{}", JSON);
        Mockito.mockStatic(RequestBody.class);
        when(RequestBody.create(anyString(), eq(JSON))).thenReturn(formBody);
        assertEquals(RequestBody.create("mn", JSON), formBody);
    }

}
