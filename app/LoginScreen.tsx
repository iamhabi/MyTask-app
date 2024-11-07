import { CommonActions } from "@react-navigation/native";
import { useState } from "react";
import { Keyboard, StyleSheet, Text, TextInput, TouchableHighlight, TouchableWithoutFeedback, View, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { MaterialIcons } from "@expo/vector-icons";

import { useAppNavigation } from '@/types/navigation';
import { ROUTES } from "@/constants/routes";

export default function LoginScreen() {
  const navigation = useAppNavigation()

  const [id, setID] = useState<string | undefined>(undefined)
  const [password, setPassword] = useState<string | undefined>(undefined)

  const dispathToHome = () => {
    navigation.dispatch(
      CommonActions.reset({
        index: 0,
        routes: [
          { name: ROUTES.HOME }
        ]
      })
    )
  }
  
  const requestLogin = () => {
    // TODO Request login

    const isLoginSuccess = true
    
    if (isLoginSuccess) {
      dispathToHome()
    } else {
      // TODO Do something if login failed
    }
  }

  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <SafeAreaView style={styles.container}>
        <View style={styles.topContainer}>
          <Pressable
            style={{
              height: '100%',
              aspectRatio: 1,
              margin: 4,
              alignItems: 'center',
              justifyContent: 'center',
            }}
            onPress={() => {
              navigation.goBack()
            }}
          >
            <MaterialIcons name="chevron-left" />
          </Pressable>
        </View>
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

        <TouchableHighlight
          style={[
            styles.buttonContainer,
            {
              margin: 16,
              alignItems: 'center',
              backgroundColor: '#0088FF',
            }
          ]}
          underlayColor='#1166DD'
          onPress={() => {
            requestLogin()
          }}
        >
          <Text style={{ color: 'white' }}>Login</Text>
        </TouchableHighlight>
      </SafeAreaView>
    </TouchableWithoutFeedback>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
  topContainer: {
    height: 48,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
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