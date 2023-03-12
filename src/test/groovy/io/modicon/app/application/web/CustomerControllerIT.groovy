package io.modicon.app.application.web

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.modicon.app.application.request.CustomerCreateRequest
import io.modicon.app.domain.model.Customer
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import java.net.http.HttpClient

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerIT extends Specification {

    @Autowired
    MockMvc mockMvc
    Gson gson

    OkHttpClient client

    void setup() {
        gson = new GsonBuilder()
                .create()
        client = new OkHttpClient()
    }

    def BASE_URL = "/api/v1/customers"
    def FULL_BASE_URL = "http://localhost:8080/api/v1/customers"

    def 'should add customer'() {
        given:
        def customer = new CustomerCreateRequest("email@mail.com", "name", 30)

        when:
        def request = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(customer)))

        then:
        request.andExpect(status().isOk())
    }

//    def 'should add customer okHttp'() {
//        given:
//        def requestBody = new FormBody.Builder()
//                .add("email", "email@mail.com")
//                .add("name", "name")
//                .add("age", "13")
//                .build()
//        def request = new Request.Builder()
//                .url(FULL_BASE_URL)
//                .post(requestBody)
//                .build()
//
//        when:
//        def call = client.newCall(request)
//        def response = call.execute()
//
//        then:
//        response.code() == 200
//    }

    def 'should not add customer'() {
        given:
        def customer = new CustomerCreateRequest("email@mail.com", "name", 30)

        when:
        def request1 = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(customer)))
        def request2 = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(customer)))

        then:
        request1.andExpect(status().isOk())
        request2.andExpect(status().isInternalServerError())
    }

    def 'should find customer by email'() {
        given:
        def customer = registerCustomer()

        when:
        def request = mockMvc.perform(get(BASE_URL + "/" + customer.email())
                .contentType(MediaType.APPLICATION_JSON))

        then:
        request.andExpect(status().isOk())
    }

    def 'should not find customer by email'() {
        when:
        def request = mockMvc.perform(get(BASE_URL + "/" + "not existed")
                .contentType(MediaType.APPLICATION_JSON))

        then:
        request.andExpect(status().isInternalServerError())
    }


    CustomerCreateRequest registerCustomer() {
        def customer = new CustomerCreateRequest("${UUID.randomUUID()}@mail.com", "name", 30)
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(customer)))
        return customer
    }

}
