//import org.junit.jupiter.api.Test;
import org.testng.Reporter;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import java.util.List;


public class TestMLFlow {
    String exp_filename="";

    @BeforeClass
    public void beforeClassTest()
    {
        // Experiment file name cannot be duplicated even if it is deleted.
        // currentTimeMillis(): current time in format of millisecond
        exp_filename = "DemoAPITest-" + System.currentTimeMillis();  // 14 length is allowed.
    }

    @Test
    public void Test_1_createExperiment() {

        Reporter.log(" Test 1 - Create one experiment.");
        String endpoint = Configuration.url+"create";

        //Send request and get response
        String strBody = "{\"name\":\"" + exp_filename + "\"}";
        var response = given().body(strBody).when().post(endpoint).then();

        //Parse JSON response.
        //RestAssured.defaultParser = Parser.JSON;
        Response  resp = response.contentType(ContentType.JSON).extract().response();
        String id = resp.jsonPath().getString("experiment_id");
        //System.out.println(" Id created is " + id);

        //Assert results
        response.assertThat().statusCode(200);
        Reporter.log("Status code is 200.");

        response.assertThat().body("experiment_id", greaterThan("0"));
        Reporter.log("The experiment id " + id +" returned.");

        Reporter.log("The experiment:"+exp_filename+" created successfully.");

         response.log().body();
        //response.log().all();
    }

    @Test
    //Get one specific experiment, parameter is required.
    //This test is to query an experiment created by Test 1.
    public void Test_2_getExperiment(){
        Reporter.log(" Test 2 - Get one specific experiment.");

        String endpoint = Configuration.url + "get-by-name";
        //System.out.println("endpoint:" + endpoint);

        //Send request and get response.
        var response =
                given().
                        queryParam("experiment_name", exp_filename).   //parameter
                        when().
                        get(endpoint).         //action
                        then();

        //response.log().body();   // to print out response results on the screen.
        //response.log().all();

        //Parse JSON response.
        Response  resp = response.contentType(ContentType.JSON).extract().response();
        String id = resp.jsonPath().getString("experiment.experiment_id");
        String name =  resp.jsonPath().getString("experiment.name");
//        System.out.println(" id got is " + id);
//        System.out.println(" name got is " + name);

        //Assert results.
        response.assertThat().statusCode(200);
        Reporter.log("Status code is 200.");

        response.assertThat().body("experiment.experiment_id", greaterThan("0"));
        Reporter.log("The experiment id " + id + " is correct.");

        response.assertThat().body("experiment.name", equalTo(exp_filename));
        Reporter.log("The experiment name " + name + " is correct.");

        Reporter.log("The experiment is got and passed verification.");
    }

    @Test
    //Get all experiments, verify the complex body.
    public void Test_3_getAllExperiments(){

        Reporter.log(" Test 3 - Get all of experiments.");
        String endpoint =Configuration.url + "list";

        //get all but no assertion
       /* //given().when().get(endpoint) - make a request.
        //then() - to retrieve response.
        var response = given().when().get(endpoint).then();

        //to print out response results on the screen.
        //response.log().body(); // to print out response results on the screen.

        response.log().all();*/

        //get all with assertion
        /*var response =
                given().
                        when().
                        get(endpoint).         //action
                        then().                        //response
                        log().                   //not a must
                        headers().              //not a must
                        assertThat().
                        statusCode(200).     //assert status code
                        header("Content-Type", equalTo("application/json")).
                        body("experiments.size()", greaterThan(0)).   //lenient test
                        body("experiments.experiment_id", everyItem(notNullValue())).
                        body("experiments.experiment_id[0]", equalTo("0"));*/

        var response = given().when().get(endpoint).then();

        //Assert results
        response.assertThat().statusCode(200);
        response.assertThat().header("Content-Type", equalTo("application/json"));
        response.assertThat().body("experiments.size()", greaterThan(0)).   //lenient test
                              body("experiments.experiment_id", everyItem(notNullValue())).
                              body("experiments.experiment_id[0]", equalTo("0"));

        //Parse JSON response
        Response  resp = response.contentType(ContentType.JSON).extract().response();
        String all_names =  resp.jsonPath().getString("experiments.name");
        Reporter.log(all_names);

        Reporter.log("All experiments got and passed verification.");

        //response.log().body();   // to print out response results on the screen.
        //response.log().all();
    }

    @Test
    public void updateAExperiment() {
        Reporter.log(" Test 3 - update one product.");
        String endpoint = "http://127.0.0.1/api_testing/product/update.php";

        //Text Block requires JAVA 15.
       /* String body = """
                {
                "id": 18,
                "name": "Water Bottle",
                "description": "Blue water bottle. Holds 64 ounces",
                "price": 28,
                "category_id": 3
                }
                """;


        var response = given().body(body).when().put(endpoint).then();
        response.log().body();*/
    }
    @Test
    public void deleteExperiment(){

        Reporter.log(" Test 4 - delete one product.");

        String endpoint = "http://127.0.0.1/api_testing/product/delete.php";

        /* String body = """
                {
                "id": 1002
                }
                """;

        var response = given().body(body).when().delete(endpoint).then();
        response.log().body();*/
    }

    @Test
    //Serialize request body into Java object.
    public void createSerializedProduct(){
        String endpoint = "http://127.0.0.1/api_testing/product/create.php";
        Product product = new Product(
                "Water Bottle",
                "Blue water bottle. Holds 64 ounces",
                120,
                3
        );
        var response = given().body(product).when().post(endpoint).then();
        response.log().body();
    }
    @Test
    public void updateSerializedProduct(){
        String endpoint = "http://127.0.0.1/api_testing/product/update.php";
        Product product = new Product(
                29,
                "Water Bottle",
                "Blue water bottle. Holds 64 ounces",
                29,
                3,
                "Active Wear - Women"
        );
        var response = given().body(product).when().put(endpoint).then();
        response.log().body();
    }

    @Test
    public void getDeserializedProduct() {
        String endpoint = "http://127.0.0.1/api_testing/product/read_one.php";
        //"id":"2","name":"Cross-Back Training Tank","description":"The most awesome phone of 2013!","price":"299.00","category_id":"2","category_name":"Active Wear - Women"}
        Product expectedProduct = new Product(
                2,
                "Cross-Back Training Tank",
                "The most awesome phone of 2013!",
                299.00,
                2,
                "Active Wear - Women"
        );

        //We want to get the response returned as a Java object.
        Product actualProduct =
                given().
                        queryParam("id", "2").
                        when().
                        get(endpoint).
                        as(Product.class);  //deserialize response body into a Java object.

        // Verify entire body returned with one easy assertion.
        assertThat(actualProduct, samePropertyValuesAs(expectedProduct));
    }

    @AfterClass
    public void teardown(){
        System.out.println("Tear down. ");
    }

}