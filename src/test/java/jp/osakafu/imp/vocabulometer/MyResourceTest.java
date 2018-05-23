package jp.osakafu.imp.vocabulometer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.stanford.nlp.ling.SentenceUtils;
import jp.osakafu.imp.vocabulometer.nlp.Main;
import jp.osakafu.imp.vocabulometer.nlp.data.LemmaTextListData;
import jp.osakafu.imp.vocabulometer.nlp.utils.JsonUtils;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.fail;

public class MyResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        System.out.println(Main.BASE_URI);
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        JsonArray array = JsonUtils.toJsonStringArray(Collections.singletonList("Hello world, I'm Clement, nice to meet you with 700£."), Function.identity());


        Entity<JsonObject> texts = Entity.json(Json.createObjectBuilder().add("texts", array).build());

        Response responseMsg = target
                .path("lemmatize")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(texts);

        System.out.println("STATUS: " + responseMsg.getStatus());

        if (responseMsg.getStatus() != 200) {
            System.out.println(responseMsg.readEntity(String.class));
            fail();
        }

        LemmaTextListData lemmaListData = responseMsg.readEntity(LemmaTextListData.class);

        assertEquals("hello world nice meet", SentenceUtils.listToString(lemmaListData.toList()));
    }
}
