package com.dlifes.textsender.screen

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream


@Serializable
data class Response(val Status: Int, val list: List<PA>)

@Serializable
data class PA(@SerialName("PA-ID")val paId: String)

@Serializable
data class sendResponse(
    val Ack_Msg: String,
    val recived: Int
)

@Serializable
data class ImageToTextResponse(
    val error: Boolean,
    val result:String?=null,
    val message:String?=null
)


class MainScreenViewModel:ViewModel(){
    var userList:MutableState<List<PA>> = mutableStateOf(emptyList())
    var text:MutableState<String> = mutableStateOf("")
    val client = HttpClient(){
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    val client2 = HttpClient(){
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    private val token = "af39d00bcf5045a8262245da6c9a5d9843ad6bd7"

    private val client3 = HttpClient() {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }


    fun getUser(){
        viewModelScope.launch {
            try {
                val response = client.get("http://www.ideetracker.com/mobapp_ws/SP_PA_List.jsp")
                Log.d("Response",response.toString())

                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val userData = Json.decodeFromString<Response>(responseBody)
                    if(userData.Status == 1){
                        userList.value = userData.list
                    }
                    else{
                        userList.value = emptyList()
                    }
                    Log.d("Response",userList.value.toString())
                }
            } catch (e: Throwable){
                println(e.message)
            }


        }
    }

    fun pushText(){
        viewModelScope.launch {
            try {
                val response = client2.post("http://www.ideetracker.com/mobapp_ws/img2text.jsp?Str_DriverID=1353&Str_PA_ID=1350&Str_img2text=CONVERTED_TEXT")
                Log.d("Response",response.toString())

                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val resData = Json.decodeFromString<sendResponse>(responseBody)
                    if(resData.recived == 1){
                        Log.d("Response",resData.Ack_Msg)
                    }
                    else{
                        //userList.value = emptyList()
                        Log.d("Response",responseBody.toString())
                    }
                    Log.d("Response",responseBody.toString())
                }
            } catch (e: Throwable){
                println(e.message)
            }
        }
    }

    fun extractText(imageByteArray:ByteArray) {
        viewModelScope.launch {
            try {
                //val base64 = encodeImage(imageBitmap)
                println("entered")
                println(token)

                val response = client3.post("https://www.imagetotext.info/api/imageToText"){
                    header("Authorization", "Bearer $token")
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("image",imageByteArray, Headers.build {
                                append(HttpHeaders.ContentType,"image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            }
                            )
                        }
                    ))
                }
                /*.post(urlString = "https://www.imagetotext.info/api/imageToText"){
                header("Authorization", "Bearer $token")
                //contentType(ContentType.Application.Json)
                //setBody(ImageBase64(base64!!))
            }*/
                if(response.status.isSuccess()){
                    val extractedText = response.body<ImageToTextResponse>()
                    if(extractedText.error){
                        text.value = extractedText.message.toString()
                    }
                    else{
                        text.value = extractedText.result.toString()
                    }
                    println(response.bodyAsText())
                }
                else{
                    println(response.bodyAsText())
                }
                println(response.toString())
            } catch (e: Throwable) {
                println(e.message + "E Thr")
                text.value = e.message.toString()
            }
        }
    }
}