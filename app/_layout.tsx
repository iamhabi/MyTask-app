import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer, RouteProp, useNavigation } from '@react-navigation/native';
import { createNativeStackNavigator, NativeStackNavigationProp } from '@react-navigation/native-stack';

import HomeScreen from './HomeScreen';
import TaskDetailScreen from './TaskDetailScreen';
import LoginScreen from './LoginScreen';
import RegisterScreen from './RegisterScreen';

import { RootStackParamList } from '@/types/navigation';
import { ROUTES } from '@/constants/routes';
import MainScreen from './MainScreen';
import { TaskProvider } from '@/hooks/TaskContext';
import { ServerProvider } from '@/hooks/ServerContext';

const Stack = createNativeStackNavigator<RootStackParamList>()

export default function RootLayout() {
  const initialRouteName = getInitialRouteName()

  return (
    <TaskProvider>
      <ServerProvider>
        <SafeAreaProvider>
          <NavigationContainer independent={true}>
            <Stack.Navigator
              initialRouteName={initialRouteName}
              screenOptions={{ headerShown: false }}
            >
              <Stack.Screen
                name={ROUTES.MAIN}
                component={MainScreen}
              />
              <Stack.Screen
                name={ROUTES.LOGIN}
                component={LoginScreen}
              />
              <Stack.Screen
                name={ROUTES.REGISTER}
                component={RegisterScreen}
              />
              <Stack.Screen
                name={ROUTES.HOME}
                component={HomeScreen}
              />
              <Stack.Screen
                name={ROUTES.DETAIL}
                component={TaskDetailScreen}
              />
            </Stack.Navigator>
          </NavigationContainer>
        </SafeAreaProvider>
      </ServerProvider>
    </TaskProvider>
  )
}

function getInitialRouteName() {
  return isLoginValid() ? ROUTES.HOME : ROUTES.MAIN
}

function isLoginValid() {
  return false
}