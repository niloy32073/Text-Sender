package com.dlifes.textsender.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import com.dlifes.textsender.components.DropdownMenuWithLabel
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap

@Composable
fun MainScreen(viewModel: MainScreenViewModel){
    val s = "S"
    var id by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var imageByteArray by remember { mutableStateOf<ByteArray?>(null) }
    viewModel.getUser()

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                imageByteArray = it
                image = it.toImageBitmap()
                viewModel.extractText(imageByteArray!!)
            }
        }
    )
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = viewModel.text.value,
            modifier = Modifier
                .padding(2.dp)
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(2.dp))
                .fillMaxWidth(.9f)
                .fillMaxHeight(.75f)
                .verticalScroll(rememberScrollState(0)),
        )
        DropdownMenuWithLabel(label = "PA-ID", itemList = viewModel.userList.value, onSelectedValueChange = {value->id = value})
        Button(onClick = { singleImagePicker.launch() }, modifier = Modifier.fillMaxWidth(.9f), shape = RoundedCornerShape(5.dp)) {
            Text(text = "Add Image")
        }
        Button(onClick = { viewModel.pushText()}, enabled = viewModel.text.value.isNotEmpty(), modifier = Modifier.fillMaxWidth(.9f), shape = RoundedCornerShape(5.dp)) {
            Text(text = "Submit Data")
        }
    }
}

