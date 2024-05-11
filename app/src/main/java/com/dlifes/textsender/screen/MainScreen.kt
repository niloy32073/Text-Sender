package com.dlifes.textsender.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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

    val multipleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Multiple(),
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.forEach {
                println(it)
            }
            viewModel.extractText(byteArrays)
        }
    )
    Box (modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Extracting Text : ${viewModel.statusE.value.first} / ${viewModel.statusE.value.second}")
            Text(text = "Pushing Text : ${viewModel.statusP.value.first} / ${viewModel.statusP.value.second}")
            Text(
                text = if(viewModel.error.value.isEmpty()) viewModel.text.value.toString() else viewModel.error.value,
                modifier = Modifier
                    .padding(2.dp)
                    .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(2.dp))
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.75f)
                    .verticalScroll(rememberScrollState(0)),
            )
            DropdownMenuWithLabel(label = "PA-ID", itemList = viewModel.userList.value, onSelectedValueChange = {value->id = value})
            Button(onClick = { multipleImagePicker.launch() }, enabled = viewModel.statusE.value.first == viewModel.statusE.value.second,modifier = Modifier.fillMaxWidth(.9f), shape = RoundedCornerShape(5.dp)) {
                Text(text = "Add Image")
            }
            Button(onClick = { viewModel.pushText(id)}, enabled = viewModel.text.value.isNotEmpty() && id.isNotEmpty(), modifier = Modifier.fillMaxWidth(.9f), shape = RoundedCornerShape(5.dp)) {
                Text(text = "Submit Data")
            }
        }
        if(viewModel.progressStatus.value){
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(modifier = Modifier.width(64.dp), color = MaterialTheme.colorScheme.onSecondary, trackColor = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

}

