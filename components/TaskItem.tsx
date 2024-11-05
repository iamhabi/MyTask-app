import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";

import BouncyCheckbox from "react-native-bouncy-checkbox";

export interface Task {
  id: number;
  title: string;
  description: string | undefined;
  dueDate: Date | undefined;
};

export type Props = {
  task: Task;
  onDelete: (task: Task) => void
}

export default function TaskItem({ task, onDelete }: Props) {
  const [isChecked, setIsChecked] = useState(false);

  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <View style={{
        flexDirection: 'row',
        alignItems: 'flex-start'
      }}>
        <BouncyCheckbox
          disableText
          fillColor="#0088FF"
          onPress={(isChecked) => {
            setIsChecked(isChecked)
          }}
        />

        <Pressable
          style={{
            flex: 1,
          }}
          onPress={() => {
            navigation.navigate('Detail', { taskJSON: JSON.stringify(task) })
          }}
        >
          <View style={styles.smallContainer}>
            <Text style={{
              flex: 1,
              textDecorationLine: isChecked ? 'line-through' : "none"
            }}>
              {task.title}
            </Text>

            <Pressable onPress={() => {
              onDelete(task)
            }}>
              <MaterialIcons
                name="delete"
                color='#DD1111'
                size={20}
                style={{ minWidth: '5%' }}
              />
            </Pressable>
          </View>

          {
            task.dueDate !== undefined ? (
              <View style={styles.smallContainer}>
                <MaterialIcons name="date-range" style={{ marginEnd: 4 }} />
                <Text>
                  {task.dueDate.toISOString()}
                </Text>
              </View>
            ) : null
          }

          {
            task.description !== undefined ? (
              <View style={styles.smallContainer}>
                <Text
                  style={{ width: '95%' }}
                  numberOfLines={2}
                  ellipsizeMode="tail"
                >
                  {task.description}
                </Text>
              </View>
            ) : null
          }
        </Pressable>
        
      </View>

      <View
        style={{
          height: 0.5,
          backgroundColor: "#000",
          marginTop: 8,
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    minHeight: 48,
    marginTop: 16,
  },
  smallContainer: {
    minHeight: 24,
    marginStart: 12,
    marginBottom: 8,
    flexDirection: 'row',
    alignItems: 'center',
  },
})