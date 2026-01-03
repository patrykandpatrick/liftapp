package com.patrykandpatrick.liftapp.ui.icons

/*
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.FinishFlag: ImageVector by lazy {
    ImageVector.Builder(
            name = "finish-flag",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        .apply {
            path(fill = SolidColor(Color(0xFF000000)), pathFillType = PathFillType.EvenOdd) {
                moveTo(8f, 1f)
                curveTo(6.48541f, 1f, 5.01148f, 1.49164f, 3.7998f, 2.40039f)
                curveTo(3.55156f, 2.58665f, 3.34974f, 2.82787f, 3.21094f, 3.10547f)
                curveTo(3.07208f, 3.38318f, 3f, 3.68951f, 3f, 4f)
                verticalLineTo(22f)
                curveTo(3f, 22.5523f, 3.44772f, 23f, 4f, 23f)
                curveTo(4.55228f, 23f, 5f, 22.5523f, 5f, 22f)
                verticalLineTo(16f)
                curveTo(5.86232f, 15.3532f, 6.91437f, 15f, 8f, 15f)
                curveTo(9.28069f, 15f, 10.3608f, 15.4215f, 11.6289f, 15.9287f)
                curveTo(12.8607f, 16.4214f, 14.2809f, 17f, 16f, 17f)
                lineTo(16.2832f, 16.9941f)
                curveTo(17.6976f, 16.9369f, 19.0642f, 16.4516f, 20.2002f, 15.5996f)
                curveTo(20.4484f, 15.4133f, 20.6503f, 15.1721f, 20.7891f, 14.8945f)
                curveTo(20.9279f, 14.6168f, 21f, 14.3105f, 21f, 14f)
                verticalLineTo(4f)
                curveTo(21f, 3.62858f, 20.8964f, 3.26419f, 20.7012f, 2.94824f)
                curveTo(20.5059f, 2.63242f, 20.2266f, 2.377f, 19.8945f, 2.21094f)
                curveTo(19.5623f, 2.04483f, 19.1902f, 1.97446f, 18.8203f, 2.00781f)
                curveTo(18.4504f, 2.04118f, 18.0969f, 2.17755f, 17.7998f, 2.40039f)
                curveTo(17.3288f, 2.75334f, 16.5524f, 3f, 15.333f, 3f)
                curveTo(14.4168f, 3f, 13.5449f, 2.61009f, 12.3105f, 2.08105f)
                curveTo(11.1391f, 1.57899f, 9.72308f, 1f, 8f, 1f)
                close()
                moveTo(13f, 12.4482f)
                curveTo(13.6178f, 12.6596f, 14.2808f, 12.8424f, 15f, 12.9346f)
                verticalLineTo(14.9131f)
                curveTo(14.3258f, 14.7991f, 13.6826f, 14.5796f, 13f, 14.3164f)
                verticalLineTo(12.4482f)
                close()
                moveTo(19f, 14f)
                curveTo(18.4048f, 14.4464f, 17.7208f, 14.7502f, 17f, 14.8975f)
                verticalLineTo(12.9258f)
                curveTo(17.6941f, 12.8256f, 18.3676f, 12.6198f, 19f, 12.3203f)
                verticalLineTo(14f)
                close()
                moveTo(7f, 13.0742f)
                curveTo(6.30523f, 13.1749f, 5.63105f, 13.3774f, 5f, 13.6768f)
                verticalLineTo(12.0029f)
                curveTo(5.59399f, 11.5571f, 6.27687f, 11.2492f, 7f, 11.1016f)
                verticalLineTo(13.0742f)
                close()
                moveTo(9f, 11.0859f)
                curveTo(9.67414f, 11.1998f, 10.3174f, 11.4195f, 11f, 11.6826f)
                verticalLineTo(13.5508f)
                curveTo(10.3822f, 13.3395f, 9.71913f, 13.1566f, 9f, 13.0645f)
                verticalLineTo(11.0859f)
                close()
                moveTo(17f, 12.9258f)
                curveTo(16.763f, 12.96f, 16.5239f, 12.9844f, 16.2832f, 12.9941f)
                lineTo(16f, 13f)
                curveTo(15.6549f, 13f, 15.3219f, 12.9758f, 15f, 12.9346f)
                verticalLineTo(10.9131f)
                curveTo(15.3239f, 10.9678f, 15.6546f, 11f, 16f, 11f)
                lineTo(16.2021f, 10.9961f)
                curveTo(16.4714f, 10.9852f, 16.738f, 10.951f, 17f, 10.8975f)
                verticalLineTo(12.9258f)
                close()
                moveTo(11f, 9.55078f)
                curveTo(11.4832f, 9.71607f, 11.939f, 9.89844f, 12.3711f, 10.0713f)
                curveTo(12.5851f, 10.1569f, 12.7948f, 10.2373f, 13f, 10.3164f)
                verticalLineTo(12.4482f)
                curveTo(12.5169f, 12.283f, 12.0609f, 12.1015f, 11.6289f, 11.9287f)
                curveTo(11.4148f, 11.8431f, 11.2053f, 11.7618f, 11f, 11.6826f)
                verticalLineTo(9.55078f)
                close()
                moveTo(8f, 9f)
                curveTo(8.34505f, 9f, 8.67815f, 9.02321f, 9f, 9.06445f)
                verticalLineTo(11.0859f)
                curveTo(8.67617f, 11.0312f, 8.34539f, 11f, 8f, 11f)
                curveTo(7.66181f, 11f, 7.32692f, 11.0348f, 7f, 11.1016f)
                verticalLineTo(9.07227f)
                curveTo(7.32946f, 9.02473f, 7.66358f, 9.00001f, 8f, 9f)
                close()
                moveTo(13f, 8.44824f)
                curveTo(13.6178f, 8.65959f, 14.2808f, 8.84238f, 15f, 8.93457f)
                verticalLineTo(10.9131f)
                curveTo(14.3258f, 10.7992f, 13.6826f, 10.5796f, 13f, 10.3164f)
                verticalLineTo(8.44824f)
                close()
                moveTo(19f, 10f)
                lineTo(18.7725f, 10.1611f)
                curveTo(18.2331f, 10.5204f, 17.6306f, 10.7687f, 17f, 10.8975f)
                verticalLineTo(8.92578f)
                curveTo(17.6941f, 8.82556f, 18.3676f, 8.61978f, 19f, 8.32031f)
                verticalLineTo(10f)
                close()
                moveTo(7f, 9.07227f)
                curveTo(6.30516f, 9.17258f, 5.63112f, 9.37668f, 5f, 9.67578f)
                verticalLineTo(8.00293f)
                curveTo(5.59399f, 7.55712f, 6.27687f, 7.24923f, 7f, 7.10156f)
                verticalLineTo(9.07227f)
                close()
                moveTo(9f, 7.08594f)
                curveTo(9.67414f, 7.19984f, 10.3174f, 7.41947f, 11f, 7.68262f)
                verticalLineTo(9.55078f)
                curveTo(10.3822f, 9.33946f, 9.7191f, 9.15662f, 9f, 9.06445f)
                verticalLineTo(7.08594f)
                close()
                moveTo(17f, 8.92578f)
                curveTo(16.763f, 8.96f, 16.5239f, 8.9844f, 16.2832f, 8.99414f)
                lineTo(16f, 9f)
                curveTo(15.6549f, 9f, 15.3219f, 8.97582f, 15f, 8.93457f)
                verticalLineTo(6.91309f)
                curveTo(15.3239f, 6.96783f, 15.6546f, 7f, 16f, 7f)
                lineTo(16.2021f, 6.99609f)
                curveTo(16.4714f, 6.9852f, 16.738f, 6.95097f, 17f, 6.89746f)
                verticalLineTo(8.92578f)
                close()
                moveTo(11f, 5.55078f)
                curveTo(11.4832f, 5.71607f, 11.939f, 5.89844f, 12.3711f, 6.07129f)
                curveTo(12.5851f, 6.15688f, 12.7948f, 6.23731f, 13f, 6.31641f)
                verticalLineTo(8.44824f)
                curveTo(12.5169f, 8.28298f, 12.0609f, 8.10152f, 11.6289f, 7.92871f)
                curveTo(11.4148f, 7.84307f, 11.2053f, 7.76175f, 11f, 7.68262f)
                verticalLineTo(5.55078f)
                close()
                moveTo(8f, 5f)
                curveTo(8.34505f, 5f, 8.67815f, 5.02321f, 9f, 5.06445f)
                verticalLineTo(7.08594f)
                curveTo(8.67617f, 7.03121f, 8.34539f, 7f, 8f, 7f)
                curveTo(7.66181f, 7.00001f, 7.32692f, 7.03478f, 7f, 7.10156f)
                verticalLineTo(5.07227f)
                curveTo(7.32946f, 5.02473f, 7.66358f, 5.00001f, 8f, 5f)
                close()
                moveTo(13f, 4.52539f)
                curveTo(13.6284f, 4.75478f, 14.2976f, 4.94124f, 15f, 4.9873f)
                verticalLineTo(6.91309f)
                curveTo(14.3258f, 6.79916f, 13.6826f, 6.57959f, 13f, 6.31641f)
                verticalLineTo(4.52539f)
                close()
                moveTo(19f, 6f)
                lineTo(18.7725f, 6.16113f)
                curveTo(18.2331f, 6.52041f, 17.6306f, 6.76871f, 17f, 6.89746f)
                verticalLineTo(4.84766f)
                curveTo(17.6787f, 4.71163f, 18.2915f, 4.47955f, 18.8174f, 4.12891f)
                lineTo(19f, 4f)
                verticalLineTo(6f)
                close()
                moveTo(7f, 5.07227f)
                curveTo(6.30516f, 5.17258f, 5.63112f, 5.37668f, 5f, 5.67578f)
                verticalLineTo(4f)
                curveTo(5.59528f, 3.55354f, 6.27903f, 3.24775f, 7f, 3.10059f)
                verticalLineTo(5.07227f)
                close()
                moveTo(9f, 3.08594f)
                curveTo(9.68927f, 3.20317f, 10.3384f, 3.43032f, 11f, 3.7002f)
                verticalLineTo(5.55078f)
                curveTo(10.3822f, 5.33946f, 9.7191f, 5.15662f, 9f, 5.06445f)
                verticalLineTo(3.08594f)
                close()
            }
        }
        .build()
}

@LightAndDarkThemePreview
@Composable
private fun FinishFlagPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.FinishFlag, null) } }
}
