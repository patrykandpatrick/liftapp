package com.patrykandpatryk.liftapp.core.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.withColor
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(value = false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val clearFocusAndValue = {
        focusManager.clearFocus()
        if (value.isNotEmpty()) onValueChange("")
    }.also { BackHandler(onBack = it) }

    Surface(
        tonalElevation = 3.dp,
        shape = CircleShape,
        modifier = modifier.height(MaterialTheme.dimens.height.searchBar),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable { if (focused) clearFocusAndValue() else focusRequester.requestFocus() }
                    .fillMaxHeight()
                    .aspectRatio(ratio = 1f),
            ) {
                Crossfade(targetState = focused) { targetState ->

                    Icon(
                        imageVector = if (targetState) Icons.Default.ArrowBack else Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Box(contentAlignment = Alignment.CenterStart) {

                if (value.isEmpty()) {

                    Text(
                        text = stringResource(id = R.string.generic_search),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.withColor { onSurface },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester = focusRequester)
                        .onFocusChanged { focusState -> focused = focusState.isFocused },
                )
            }
        }
    }
}
