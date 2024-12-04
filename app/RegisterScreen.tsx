import { useState } from "react";
import { Keyboard, StyleSheet, TouchableWithoutFeedback, TextInput, TouchableHighlight, View, Text, Pressable, ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { MaterialIcons } from "@expo/vector-icons";
import { useAppNavigation } from '@/types/navigation';
import { Colors } from "@/constants/Colors";
import { useServerContext } from "@/hooks/ServerContext";
import { CommonActions } from "@react-navigation/native";
import { ROUTES } from "@/constants/routes";
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function RegisterScreen() {
  const [username, setUsername] = useState<string>('')
  const [email, setEmail] = useState<string>('')
  const [password1, setPassword1] = useState<string>('')
  const [password2, setPassword2] = useState<string>('')

  const [usernameErrorMessage, setUsernameErrorMessage] = useState<string>('')
  const [emailErrorMessage, setEmailErrorMessage] = useState<string>('')
  const [passwordErrorMessage, setPasswordErrorMessage] = useState<string>('')

  const [isLoading, setIsLoading] = useState<boolean>(false)

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
    setIsLoading(true)

    serverContext.register(
      username, email, password1, password2,
      () => {
        serverContext.login(
          username, password1,
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
    
            console.log(error)

            // TODO do something when login failed
          },
        )
      },
      (error) => {
        setIsLoading(false)

        if ('username' in error) {
          setUsernameErrorMessage(error['username'] as string)
        } else {
          setUsernameErrorMessage('')
        }
       
        if ('email' in error) {
          setEmailErrorMessage(error['email'] as string)
        } else {
          setEmailErrorMessage('')
        }

        if ('password1' in error) {
          setPasswordErrorMessage(error['password1'] as string)
        }

        if ('password' in error) {
          setPasswordErrorMessage(error['password'] as string)
        }

        if (!error.hasOwnProperty('password') && !error.hasOwnProperty('password1')) {
          setPasswordErrorMessage('')
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

        {
          usernameErrorMessage !== '' ? (
            <Text style={styles.errorMessage}>{usernameErrorMessage}</Text>
          ) : null
        }

        <TextInput
          style={styles.viewContainer}
          onChangeText={setEmail}
          value={email}
          placeholder="E-mail"
        />

        {
          emailErrorMessage !== '' ? (
            <Text style={styles.errorMessage}>{emailErrorMessage}</Text>
          ) : null
        }
        
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

        {
          passwordErrorMessage !== '' ? (
            <Text style={styles.errorMessage}>{passwordErrorMessage}</Text>
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
              requestRegister()
            }
          }}
        >
          {
            !isLoading ? (
              <Text style={{ color: 'white' }}>Register</Text>
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