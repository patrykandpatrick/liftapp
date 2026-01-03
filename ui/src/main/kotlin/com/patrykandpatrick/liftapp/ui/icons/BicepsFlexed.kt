package com.patrykandpatrick.liftapp.ui.icons /*
                                              ISC License

                                              Copyright (c) for portions of Lucide are held by Cole Bemis 2013-2023 as part of Feather (MIT). All other copyright (c) for Lucide are held by Lucide Contributors 2025.

                                              Permission to use, copy, modify, and/or distribute this software for any
                                              purpose with or without fee is hereby granted, provided that the above
                                              copyright notice and this permission notice appear in all copies.

                                              THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
                                              WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
                                              MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
                                              ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
                                              WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
                                              ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
                                              OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

                                              ---

                                              The MIT License (MIT) (for portions derived from Feather)

                                              Copyright (c) 2013-2023 Cole Bemis

                                              Permission is hereby granted, free of charge, to any person obtaining a copy
                                              of this software and associated documentation files (the "Software"), to deal
                                              in the Software without restriction, including without limitation the rights
                                              to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                                              copies of the Software, and to permit persons to whom the Software is
                                              furnished to do so, subject to the following conditions:

                                              The above copyright notice and this permission notice shall be included in all
                                              copies or substantial portions of the Software.

                                              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                                              IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                                              FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                                              AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                                              LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                                              OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                                              SOFTWARE.

                                              */

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.BicepsFlexed: ImageVector by lazy {
    ImageVector.Builder(
            name = "biceps-flexed",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12.409f, 13.017f)
                arcTo(5f, 5f, 0f, false, true, 22f, 15f)
                curveToRelative(0f, 3.866f, -4f, 7f, -9f, 7f)
                curveToRelative(-4.077f, 0f, -8.153f, -0.82f, -10.371f, -2.462f)
                curveToRelative(-0.426f, -0.316f, -0.631f, -0.832f, -0.62f, -1.362f)
                curveTo(2.118f, 12.723f, 2.627f, 2f, 10f, 2f)
                arcToRelative(3f, 3f, 0f, false, true, 3f, 3f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                curveToRelative(-1.105f, 0f, -1.64f, -0.444f, -2f, -1f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(15f, 14f)
                arcToRelative(5f, 5f, 0f, false, false, -7.584f, 2f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9.964f, 6.825f)
                curveTo(8.019f, 7.977f, 9.5f, 13f, 8f, 15f)
            }
        }
        .build()
}

@LightAndDarkThemePreview
@Composable
private fun BicepsFlexedPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.BicepsFlexed, null) } }
}
