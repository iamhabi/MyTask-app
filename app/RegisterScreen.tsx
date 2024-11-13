import { useState } from "react";
import { Keyboard, StyleSheet, TouchableWithoutFeedback, TextInput, TouchableHighlight, View, Text, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { MaterialIcons } from "@expo/vector-icons";
import { useAppNavigation } from '@/types/navigation';
import { Colors } from "@/constants/Colors";
import { useServerContext } from "@/hooks/ServerContext";
import { CommonActions } from "@react-navigation/native";
import { ROUTES } from "@/constants/routes";

export default function RegisterScreen() {
  const [username, setUsername] = useState<string>('')
  const [email, setEmail] = useState<string>('')
  const [password1, setPassword1] = useState<string>('')
  const [password2, setPassword2] = useState<string>('')

  const navigation = useAppNavigation()

  const serverContext = useServerContext()

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
  
  const requestRegister = () => {
    serverContext.register(
      username, email, password1, password2,
      () => {
        console.log('register success')

        serverContext.login(
          username, password1,
          (access, refresh, user_id) => {
            dispathToHome()
          },
          (error) => {
            console.log(error)
    
            // TODO do something when login failed
          },
        )
      },
      (error) => {
        console.log(error)

        // TODO do something when register failed
      },
    )
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
          style={styles.viewContainer}
          onChangeText={setUsername}
          value={username}
          placeholder="Username"
          autoFocus
        />

        <TextInput
          style={styles.viewContainer}
          onChangeText={setEmail}
          value={email}
          placeholder="E-mail"
        />

        <TextInput
          style={styles.viewContainer}
          onChangeText={setPassword1}
          value={password1}
          placeholder="Password"
          secureTextEntry={true}
        />

        <TextInput
          style={styles.viewContainer}
          onChangeText={setPassword2}
          value={password2}
          placeholder="Repeat password"
          secureTextEntry={true}
        />

        <TouchableHighlight
          style={[
            styles.viewContainer,
            {
              borderWidth: 0,
              alignItems: 'center',
              justifyContent: 'center',
              backgroundColor: Colors.primary,
            }
          ]}
          underlayColor={Colors.loginBtnUnderlay}
          onPress={() => {
            requestRegister()
          }}
        >
          <Text style={{ color: 'white' }}>Register</Text>
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
  viewContainer: {
    minHeight: 48,
    borderWidth: 1,
    borderRadius: 8,
    padding: 8,
    margin: 16,
  },
})