import { CommonActions, useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { Keyboard, StyleSheet, Text, TextInput, TouchableHighlight, TouchableWithoutFeedback, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function LoginScreen() {
  const [id, setID] = useState<string | undefined>(undefined)
  const [password, setPassword] = useState<string | undefined>(undefined)

  const navigation = useNavigation()
  
  const requestLogin = () => {
    // TODO Request login

    const isLoginSuccess = true
    
    if (isLoginSuccess) {
      navigation.dispatch(
        CommonActions.reset({
          index: 0,
          routes: [
            { name: 'Home' }
          ]
        })
      )
    } else {
      // TODO Do something if login failed
    }
  }

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <SafeAreaView style={styles.container}>
        <TextInput
          style={styles.inputFieldContainer}
          onChangeText={(text) => {
            setID(text !== '' ? text : undefined)
          }}
          value={id}
          placeholder="ID"
          autoFocus
        />

        <TextInput
          style={styles.inputFieldContainer}
          onChangeText={(text) => {
            setPassword(text !== '' ? text : undefined)
          }}
          value={password}
          placeholder="Password"
        />

        <View style={{
            margin: 16,
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          <TouchableHighlight
            style={[
              styles.buttonContainer,
              {
                borderWidth: 0.5,
                backgroundColor: '#EEEEEE',
              }
            ]}
            underlayColor='#CECECE'
            onPress={() => {
              navigation.navigate('Register', {})
            }}
          >
            <Text>Register</Text>
          </TouchableHighlight>

          <TouchableHighlight
            style={[
              styles.buttonContainer,
              { backgroundColor: '#0088FF', }
            ]}
            underlayColor='#1166DD'
            onPress={() => {
              requestLogin()
            }}
          >
            <Text style={{ color: 'white' }}>Login</Text>
          </TouchableHighlight>
        </View>
      </SafeAreaView>
    </TouchableWithoutFeedback>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
  inputFieldContainer: {
    borderWidth: 1,
    borderRadius: 8,
    padding: 8,
    margin: 16,
  },
  buttonContainer: {
    borderRadius: 8,
    padding: 8,
  },
})