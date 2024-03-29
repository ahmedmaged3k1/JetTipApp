package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                MyApp {
                    MainContent()

                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(

        color = MaterialTheme.colors.background
    ) {
        content()


    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent() {
    Column() {
        BillForm()

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmmountState = remember {
        mutableStateOf(0.0)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val range = IntRange(start = 1, endInclusive = 100)
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }


    TopHeader(totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top

        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    // On Change
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split ",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1
                                    else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBillState.value.toDouble(),
                                        splitByState.value,
                                        tipPercentage
                                    )

                            })
                        Text(
                            text = "${splitByState.value}",
                            fontWeight = ExtraBold,
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1
                                }
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBillState.value.toDouble(),
                                        splitByState.value,
                                        tipPercentage
                                    )

                            })


                    }
                }
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Tip ",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = " $ ${tipAmmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)

                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = " $tipPercentage %  ",

                        )
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(value = sliderPositionState.value,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),

                        onValueChange = {
                            sliderPositionState.value = it
                            tipAmmountState.value =
                                calculateTotalTip(
                                    totalBillState.value.toDouble(),
                                    tipPercentage
                                )
                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    splitByState.value,
                                    tipPercentage
                                )
                        },
                        steps = 5,
                        onValueChangeFinished = {

                        }
                    )
                }

            }
        }

    }
}


//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    androidx.compose.material.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)

    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                fontWeight = Bold
            )
            Text(
                text = "$ $total ",
                style = MaterialTheme.typography.h4,
                fontWeight = ExtraBold

            )

        }


    }

}


