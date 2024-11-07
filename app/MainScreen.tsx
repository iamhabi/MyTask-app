import { useAppNavigation } from "@/types/navigation";
import { StyleSheet, Text, TouchableHighlight, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { CommonActions } from "@react-navigation/native";
import { ROUTES } from "@/constants/routes";

export default function MainScreen() {
  const navigation = useAppNavigation()

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.topContainer}>
        <Text>My todo</Text>
      </View>
      <TouchableHighlight
        style={styles.buttonContainer}
        underlayColor={'#CECECE'}
        onPress={() => {
          navigation.dispatch(
            CommonActions.reset({
              index: 0,
              routes: [
                { name: ROUTES.HOME }
              ]
            })
          )
        }}
      >
        <Text>Start without login</Text>
      </TouchableHighlight>

      <TouchableHighlight
        style={styles.buttonContainer}
        underlayColor='#CECECE'
        onPress={() => {
          navigation.navigate(ROUTES.LOGIN)
        }}
      >
        <Text>Login</Text>
      </TouchableHighlight>

      <TouchableHighlight
        style={styles.buttonContainer}
        underlayColor='#CECECE'
        onPress={() => {
          navigation.navigate(ROUTES.REGISTER)
        }}
      >
        <Text>Register</Text>
      </TouchableHighlight>
    </SafeAreaView>
  );
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
    justifyContent: 'center',
  },
  buttonContainer: {
    borderWidth: 1,
    borderRadius: 8,
    padding: 16,
    margin: 16,
  }
})