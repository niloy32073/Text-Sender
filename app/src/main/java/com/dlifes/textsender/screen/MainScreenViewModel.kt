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
    var text:MutableState<List<String>> = mutableStateOf(emptyList())
    var statusE:MutableState<Pair<Int,Int>> = mutableStateOf(Pair(0,0))
    var progressStatus:MutableState<Boolean> = mutableStateOf(false)
    var statusP:MutableState<Pair<Int,Int>> = mutableStateOf(Pair(0,0))
    var immutableText= emptyList<String>()
    var error:MutableState<String> = mutableStateOf("")
    val data = " hello #512 & * ©"
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

    init {
        getUser()
    }

    fun cleanString(input:String):String{
        val allowedChars = setOf('a', 'b', 'c', 'd','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',',':', ';', '-', '_','<','/','>')
        val newString = input.filter { it in allowedChars }
        val newString2 = newString.replace("<br />","~")
        val output = newString2.replace("PM","PM!")
        return output
    }
    fun getUser(){
        viewModelScope.launch {
            try {
                val response = client.get("http://www.ideetracker.com/mobapp_ws/SP_PA_List.jsp")
                Log.d("Response",response.toString())
                println(data)
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

    fun pushText(id:String){
        println(immutableText.size)
        statusP.value=Pair(0,immutableText.size)
        progressStatus.value = true
        viewModelScope.launch {
            immutableText.forEach { it->
                statusP.value= Pair(immutableText.indexOf(it)+1,immutableText.size)
                try {
                    val response = client2.post("http://www.ideetracker.com/mobapp_ws/img2text.jsp?Str_DriverID=1353&Str_PA_ID=$id&Str_img2text=$it")
                    Log.d("Response",response.toString())
                    print(it)
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
            text.value = emptyList()
            error.value = "Submitted successfully"
            progressStatus.value = false
        }
    }

    fun extractText(imageByteArrays:List<ByteArray>) {
        viewModelScope.launch {
            error.value = ""
            immutableText= emptyList()
            statusE.value= Pair(0,imageByteArrays.size)
            progressStatus.value = true
            imageByteArrays.forEach{it->
                statusE.value= Pair(imageByteArrays.indexOf(it)+1,imageByteArrays.size)
                try {
                    //val base64 = encodeImage(imageBitmap)
                    println("entered")
                    println(token)

                    val response = client3.post("https://www.imagetotext.info/api/imageToText"){
                        header("Authorization", "Bearer $token")
                        setBody(MultiPartFormDataContent(
                            formData {
                                append("image",it, Headers.build {
                                    append(HttpHeaders.ContentType,"image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                                }
                                )
                            }
                        ))
                    }

                    if(response.status.isSuccess()){
                        val extractedText = response.body<ImageToTextResponse>()
                        if(extractedText.error){
                            error.value = extractedText.message.toString()
                            immutableText = emptyList()
                            return@forEach
                        }
                        else{
                            immutableText = immutableText + cleanString(extractedText.result.toString())
                        }
                        println(response.bodyAsText())
                    }
                    else{
                        error.value = response.bodyAsText()
                        immutableText = emptyList()
                        println(response.bodyAsText())
                        return@forEach
                    }
                    println(response.toString())
                } catch (e: Throwable) {
                    println(e.message + "E Thr")
                    error.value = e.message.toString()
                    immutableText = emptyList()
                    return@forEach
                }
            }

            text.value = immutableText
            println(text.value.size)
            progressStatus.value = false
        }
    }
}