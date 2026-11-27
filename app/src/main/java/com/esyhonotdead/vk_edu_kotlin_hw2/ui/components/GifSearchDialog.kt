package com.esyhonotdead.vk_edu_kotlin_hw2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.esyhonotdead.vk_edu_kotlin_hw2.R
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.RATING_G
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.RATING_PG
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.RATING_PG_13
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.RATING_R
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.icons.search
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.theme.Dimens

val ratings = listOf(
    RATING_G to R.string.gif_rating_g,
    RATING_PG to R.string.gif_rating_pg,
    RATING_PG_13 to R.string.gif_rating_pg_13,
    RATING_R to R.string.gif_rating_r
)
private const val MaxQueryLength = 50

@Composable
fun GifSearchDialog(
    initialQuery: String,
    initialRating: String,
    onDismiss: () -> Unit,
    onSearch: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val query = rememberTextFieldState(initialText = initialQuery)
    var selectedRating by rememberSaveable { mutableStateOf(initialRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Text(stringResource(R.string.search_gifs))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.sizeM)
            ) {
                OutlinedTextField(
                    state = query,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.gif_search_query)) },
                    leadingIcon = { Icon(imageVector = search, contentDescription = null) },
                    shape = MaterialTheme.shapes.large,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = InputTransformation.maxLength(MaxQueryLength)
                )
                Column {
                    Text(
                        text = stringResource(R.string.gif_search_rating),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.sizeS)) {
                        ratings.forEach { (rating, labelResId) ->
                            FilterChip(
                                selected = rating == selectedRating,
                                onClick = { selectedRating = rating },
                                label = { Text(stringResource(labelResId)) },
                                shape = MaterialTheme.shapes.large,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = { onSearch(query.text.toString(), selectedRating) },
                enabled = query.text.isNotBlank()
            ) {
                Text(stringResource(R.string.search))
            }
        }
    )
}
