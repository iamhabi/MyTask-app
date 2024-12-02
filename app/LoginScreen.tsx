import { CommonActions } from "@react-navigation/native";
import { useState } from "react";
import { Keyboard, StyleSheet, Text, TextInput, TouchableHighlight, TouchableWithoutFeedback, View, Pressable, ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { MaterialIcons } from "@expo/vector-icons";
import { useAppNavigation } from '@/types/navigation';
import { ROUTES } from "@/constants/routes";
import { Colors } from "@/constants/Colors";
import { useServerContext } from "@/hooks/ServerContext";
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function LoginScreen() {
  const navigation = useAppNavigation()

  const [username, setUsername] = useState<string>('')
  const [password, setPassword] = useState<string>('')

  const [errorMessage, setErrorMessage] = useState<string>('')

  const [isLoading, setIsLoading] = useState<boolean>(false)

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
  
  const requestLogin = () => {
    setIsLoading(true)

    serverContext.login(
      username, password,
      (access, refresh, user_id) => {
        try {
          AsyncStorage.setItem('access', access)
          AsyncStorage.setItem('refresh', refresh)
          AsyncStorage.setItem('user_id', user_id)
        } catch (error) {

        }

        setIsLoading(false)

        dispathToHome()
      },
      (error) => {
        setIsLoading(false)

        if (error.hasOwnProperty('detail')) {
          setErrorMessage(error.detail)
        } else {
          setErrorMessage("Error")
        }
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
          onChangeText={setPassword}
          value={password}
          placeholder="Password"
          secureTextEntry={true}
        />

        {
          errorMessage !== '' ? (
            <Text style={styles.errorMessage}>{errorMessage}</Text>
          ) : null
        }
        
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
            if (!isLoading) {
              requestLogin()
            }
          }}
        >
          {
            !isLoading ? (
              <Text style={{ color: 'white' }}>Login</Text>
            ) : (
              <ActivityIndicator color='white' />
            )
          }
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
  errorMessage: {
    minHeight: 24,
    color: 'red',
    paddingStart: 16,
  }
})