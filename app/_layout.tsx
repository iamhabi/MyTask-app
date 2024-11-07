import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer, RouteProp, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator, NativeStackNavigationProp } from '@react-navigation/native-stack';

import HomeScreen from './HomeScreen';
import TaskDetailScreen from './TaskDetailScreen';
import LoginScreen from './LoginScreen';
import RegisterScreen from './RegisterScreen';

export type RootStackParamList = {
  Login: undefined
  Register: undefined
  Home: undefined
  Detail: {
    taskJSON: string
  }
}

export type DetailNavigationProp = NativeStackNavigationProp<RootStackParamList, 'Detail'>
export type DetailRouteProp = RouteProp<RootStackParamList, 'Detail'>

const Stack = createNativeStackNavigator<RootStackParamList>()
export const useAppNavigation = () => useNavigation<NativeStackNavigationProp<RootStackParamList>>()

export default function RootLayout() {
  const initialRouteName = getInitialRouteName()

  return (
    <SafeAreaProvider>
      <NavigationContainer independent={true}>
        <Stack.Navigator
          initialRouteName={initialRouteName}
          screenOptions={{ headerShown: false }}
        >
          <Stack.Screen
            name='Login'
            component={LoginScreen}
          />
          <Stack.Screen
            name='Register'
            component={RegisterScreen}
          />
          <Stack.Screen
            name='Home'
            component={HomeScreen}
          />
          <Stack.Screen
            name='Detail'
            component={TaskDetailScreen}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  )
}

function getInitialRouteName() {
  return isLoginValid() ? 'Home' : 'Login'
}

function isLoginValid() {
  return true
}