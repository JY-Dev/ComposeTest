package com.jydev.composetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jydev.composetest.ui.theme.ComposeTestTheme

class MainActivity : ComponentActivity() {
    var messageList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                var isShowingKeyboard by remember { mutableStateOf(false) }
                val focusManager = LocalFocusManager.current
                // A surface container using the 'background' color from the theme
                Surface(color = Color.Yellow) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        Image(
                            painterResource(R.drawable.background),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Row(Modifier.align(Alignment.BottomCenter)) {
                            ChatView(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.Bottom),
                                isShowingKeyboard,
                                focusManager
                            )
                            HeartEffectView(modifier = Modifier.align(Alignment.Bottom))
                        }
                    }
                }
            }
        }
    }

    private fun addMessage(chatMessage: String) {
        messageList.add(chatMessage)
        MessageHolder.setMessageList(messageList.toList())
    }

    @Composable
    fun HeartEffectView(modifier: Modifier) {
        var heartAnimationState by remember { mutableStateOf(AnimationState.NONE)}
        Box(modifier) {
            FavoriteAnimation(animationState = heartAnimationState, modifier = Modifier.size(76.dp, 100.dp))
            HeartIconView(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                heartAnimationState = AnimationState.START
            }
        }

    }

    @Composable
    fun HeartIconView(modifier: Modifier, hearIconOnClick: () -> Unit) {
        Icon(painter = painterFavorite(), "", modifier = modifier.noRippleClickable {
            hearIconOnClick()
        })
    }

    @Composable
    fun ChatView(modifier: Modifier, isShowingKeyboard: Boolean, focusManager: FocusManager) {
        var chatMessage by remember { mutableStateOf("") }
        Column(modifier = modifier) {
            ChatList(
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 20.dp, bottom = 20.dp))
            ChatTextField(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                chatMessage,
                isShowingKeyboard,
                onMessageChange = {
                    chatMessage = it
                },
                keyboardDone = {
                    addMessage(chatMessage = chatMessage)
                    chatMessage = ""
                    focusManager.clearFocus()
                })
        }
    }

    @Composable
    fun ChatList(modifier: Modifier) {
        val messageList by MessageHolder.messageList.observeAsState(listOf())
        LazyColumn(modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth(), content = {
            val messageSubList = if (messageList.size > 4)
                messageList.subList(messageList.size - 4, messageList.size)
            else messageList

            items(messageSubList) { message ->
                Text(text = message,modifier = Modifier.padding(top = 12.dp),color = Color(0xfff2f2f2))
            }
        })
    }

    @Composable
    fun ChatTextField(
        modifier: Modifier,
        message: String,
        isShowingKeyboard: Boolean,
        onMessageChange: (String) -> Unit,
        keyboardDone: () -> Unit
    ) {
        TextField(
            value = message,
            onValueChange = onMessageChange,
            maxLines = 1,
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { keyboardDone() }),
            shape = RoundedCornerShape(75.dp),
            colors = TextFieldDefaults.textFieldColors(textColor = Color(0xfff2f2f2)),
            placeholder = {
                Text("댓글 작성하기", color = Color(0xfff2f2f2), fontSize = 14.sp)
            },
            modifier = if (isShowingKeyboard) modifier.fillMaxWidth() else modifier.border(
                width = 2.dp,
                color = Color(0xfff2f2f2),
                shape = RoundedCornerShape(75.dp)
            )
        )
    }
}


