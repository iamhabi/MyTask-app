import { RouteProp, useNavigation } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

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

export const useAppNavigation = () => useNavigation<NativeStackNavigationProp<RootStackParamList>>()