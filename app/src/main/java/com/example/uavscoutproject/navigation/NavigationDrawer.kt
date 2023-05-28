package com.example.uavscoutproject.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.R
import com.example.uavscoutproject.authentication.AuthenticationScreenViewModel

@Composable
fun DrawerHeader(viewModel: AuthenticationScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    var expand by remember { mutableStateOf(false) }
    val description = stringResource(id = R.string.sample_description)
    val overflow = if(expand) TextOverflow.Visible else TextOverflow.Ellipsis
    val correo = viewModel.getEmail()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .background(colorResource(id = R.color.profile_background_color))
    ) {

        Image(
            painter = painterResource(id = R.drawable.sample_profile_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .offset(y = -98.dp)
                .clip(RectangleShape)

        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)) {
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(
                            4.dp,
                            color = colorResource(id = R.color.profile_background_color),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.no_image),
                        contentDescription = "My Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                }
                Text(
                    text = "Nombre usuario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top  = 2.dp)
                )
                Text(
                    text = correo.toString(),
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#808080"))
                )

                Text(
                    text = "Description",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top  = 2.dp)
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    maxLines = if(expand) Int.MAX_VALUE else 1,
                    overflow = overflow,
                    textAlign = TextAlign.Justify
                )

            }
            Box(modifier = Modifier.padding(end = 16.dp, top = 100.dp)){
                IconButton(
                    onClick = { expand = !expand },
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)

                ) {
                    Icon(
                        painter = if (expand)
                            painterResource(id = R.drawable.ic_arrow_up)
                        else painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Expand/collapse",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerBody(
    items: List<Menuitem>,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthenticationScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onItemClick: (Menuitem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            val title = item.title
            // Verificar si 'title' contiene espacios
            val espacios = " " in title
            // Obtener la primera palabra o las primeras 5 letras de 'title' dependiendo de si hay espacios o no
            val inicioTexto = if (espacios) title.indexOf(" ") else 5
            // Pintar el texto inicial en azul
            val textoPintado = buildAnnotatedString {
                withStyle(style = SpanStyle(color = colorResource(id = R.color.text_blue_Color))) {
                    append(title.take(inicioTexto))
                }
                append(title.drop(inicioTexto))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)

            ) {
                Icon(
                    painter = item.icon,
                    contentDescription = item.description,
                    modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(32.dp))

                Text(text = textoPintado, fontSize = 17.sp)
            }
        }
        item { Button(
                onClick = {
                            viewModel.clearLogIn()
                            navController.popBackStack()
                            navController.navigate(AppScreens.AuthenticationScreen.route) {
                                popUpTo(0)
                                launchSingleTop = true
                                anim {
                                    exit = android.R.anim.fade_out
                                    popExit = android.R.anim.fade_out
                                }
                            }
                          },
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.buttonColor)
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}




