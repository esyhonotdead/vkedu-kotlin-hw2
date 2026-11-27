package com.esyhonotdead.vk_edu_kotlin_hw2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifImage
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifModel
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.theme.Dimens

private const val MaxCardTextLines = 2

@Composable
fun GifCard(gif: GifModel, image: GifImage, aspectRatio: Float, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = image.url,
                contentDescription = gif.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clickable(
                        onClick = onClick
                    )
            )
            if (gif.title.isNotBlank()) {
                Text(
                    text = gif.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = MaxCardTextLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(Dimens.sizeS)
                )
            }
        }
    }
}
