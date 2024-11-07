import { useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { Keyboard, StyleSheet, TouchableWithoutFeedback, TextInput, TouchableHighlight, View, Text, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { MaterialIcons } from "@expo/vector-icons";

export default function RegisterScreen() {
  const [id, setID] = useState<string | undefined>(undefined)
  const [email, setEmail] = useState<string | undefined>(undefined)
  const [password1, setPassword1] = useState<string | undefined>(undefined)
  const [password2, setPassword2] = useState<string | undefined>(undefined)

  const navigation = useNavigation()

  const requestRegister = () => {
    // TODO Request register
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
            setEmail(text !== '' ? text : undefined)
          }}
          value={email}
          placeholder="E-mail"
        />

        <TextInput
          style={styles.inputFieldContainer}
          onChangeText={(text) => {
            setPassword1(text !== '' ? text : undefined)
          }}
          value={password1}
          placeholder="Password"
        />

        <TextInput
          style={styles.inputFieldContainer}
          onChangeText={(text) => {
            setPassword2(text !== '' ? text : undefined)
          }}
          value={password2}
          placeholder="Repeat password"
        />

        <View
          style={{
            margin: 16,
            flexDirection: 'row-reverse',
            alignItems: 'center',
          }}
        >
          <TouchableHighlight
            style={{
              padding: 8,
              borderRadius: 8,
              backgroundColor: '#0088FF'
            }}
            underlayColor='#1166DD'
            onPress={() => {
              requestRegister()
            }}
          >
            <Text style={{ color: 'white' }}>Register</Text>
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
})