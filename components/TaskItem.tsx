import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";

import BouncyCheckbox from "react-native-bouncy-checkbox";
import { Task } from "@/types/task";
import { Colors } from "@/constants/Colors";
import { useServerContext } from "@/hooks/ServerContext";

export type Props = {
  task: Task
  onClick: (task: Task) => void
}

export default function TaskItem({ task, onClick }: Props) {
  const [isChecked, setIsChecked] = useState(false);

  const { updateTask, deleteTask } = useServerContext()
  
  return (
    <View style={styles.container}>
      <View style={{
        flexDirection: 'row',
        alignItems: 'flex-start'
      }}>
        <BouncyCheckbox
          disableText
          fillColor={Colors.primary}
          onPress={(isChecked) => {
            setIsChecked(isChecked)
            
            task.is_done = isChecked

            updateTask(
              task,
              () => {
                
              },
              (error) => {
                
              }
            )
          }}
        />

        <Pressable
          style={{
            flex: 1,
          }}
          onPress={() => {
            onClick(task)
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
              deleteTask(
                task.id,
                () => {
                  
                },
                (error) => {

                }
              )
            }}>
              <MaterialIcons
                name="delete"
                color={Colors.delete}
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