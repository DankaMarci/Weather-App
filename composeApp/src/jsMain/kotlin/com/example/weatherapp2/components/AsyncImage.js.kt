// src/jsMain/kotlin/com/example/weatherapp2/components/AsyncImageJs.kt
package com.example.weatherapp2.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.web.dom.Img

//@Composable
//actual fun AsyncImage(
//    resource: String,
//    contentDescription: String?,
//    modifier: Modifier,
//    contentScale: ContentScale
//) {
//    Img(src = resource, attrs = {
//        if (contentDescription != null) attr("alt", contentDescription)
//        // Add style/size as needed
//    })
//}

@Composable
actual fun AsyncImage(
    resource: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    Img(
        src = resource,
        attrs = {
            if (contentDescription != null) attr("alt", contentDescription)
            style {
                property("width", "100px") // set your desired size here
                property("height", "100px")
            }
        }
    )
}
