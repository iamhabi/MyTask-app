import { MaterialIcons } from "@expo/vector-icons";
import { Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  icon: keyof typeof MaterialIcons.glyphMap;
  size: number;
  onPress: () => void;
};

export default function IconButton({ icon, size, onPress }: Props) {
  return (
    <View style={styles.iconButtonContainer}>
      <Pressable
        style={[
          styles.iconButton,
          {
            height: size,
            borderRadius: size,
          }
        ]}
        onPress={onPress}
      >
        <MaterialIcons name={icon} size={size / 1.5} color="#25292e" />
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  iconButtonContainer: {
    aspectRatio: 1,
    margin: 2,
  },
  iconButton: {
    aspectRatio: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#DFDFDF',
  }
});