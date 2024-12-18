import { useState } from "react";
import { useNavigation, useRoute } from "@react-navigation/native";
import { Keyboard, Pressable, StyleSheet, Text, TextInput, TouchableWithoutFeedback, View } from "react-native";
import { SafeAreaView } from 'react-native-safe-area-context';
import { MaterialIcons } from "@expo/vector-icons";

import DateTimePicker from "react-native-modal-datetime-picker";

import SubTasksScreen from "./SubTasksScreen";
import { DetailNavigationProp, DetailRouteProp } from "@/types/navigation";

import { Task } from "@/types/task";
import { Colors } from "@/constants/Colors";
import { useServerContext } from "@/hooks/ServerContext";

export default function TaskDetailScreen() {
  const navigation = useNavigation<DetailNavigationProp>()
  const route = useRoute<DetailRouteProp>()
  const { taskJSON } = route.params
  const [task, _] = useState<Task>(JSON.parse(taskJSON) as Task)

  const { updateTask, deleteTask } = useServerContext()

  const [title, setTitle] = useState<string>(task.title)
  const [description, setDescription] = useState<string | undefined>(task.description !== undefined ? task.description : undefined)
  const [dueDate, setDueDate] = useState<Date | undefined>(task.dueDate !== undefined ? new Date(task.dueDate) : undefined)

  const [isDatePickerVisible, setDatePickerVisibility] = useState(false)

  const isEdited = title !== task.title || description !== task.description || dueDate !== task.dueDate
  
  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <SafeAreaView style={styles.container}>
        <View style={styles.topContainer}>
          <Pressable
            style={styles.button}
            onPress={() => {
              navigation.goBack()
            }}
          >
            <MaterialIcons name="chevron-left" />
          </Pressable>

          <View style={{ flex: 1 }} />

          {
            isEdited ? (
              <Pressable
                style={styles.button}
                onPress={() => {
                  const updatedTask: Task = {
                    id: task.id,
                    parentId: task.parentId,
                    title: title,
                    description: description,
                    isDone: task.isDone,
                    dueDate: dueDate,
                    created: task.created
                  }

                  updateTask(
                    updatedTask,
                    () => {
                      task.title = title
                      task.description = description
                      task.dueDate = dueDate
                    },
                    (error) => {

                    }
                  )

                  Keyboard.dismiss()
                }}
              >
                <MaterialIcons name="save" />
              </Pressable>
            ) : null
          }

          <Pressable
            style={styles.button}
            onPress={() => {
              deleteTask(
                task.id,
                () => {
                  navigation.goBack()
                },
                (error) => {

                }
              )
            }}
          >
            <MaterialIcons
              name="delete"
              color={Colors.delete}
              size={20}
              style={{ minWidth: '5%' }}
            />
          </Pressable>
        </View>

        <View style={styles.baseContainer}>
          <TextInput
            onChangeText={setTitle}
            value={title}
            numberOfLines={1}
            placeholder={task.title}
          />
        </View>
        
        <Pressable
          style={[
            styles.baseContainer,
            {
              flexDirection:'row',
              alignItems: 'center',
              justifyContent: 'flex-start',
            }
          ]}
          onPress={() => {
            setDatePickerVisibility(true)
          }}
        >
          <MaterialIcons name="date-range" style={{ marginEnd: 4 }}/>
          <Text>{dueDate === undefined ? "Not setted" :dueDate.toISOString()}</Text>
        </Pressable>

        <DateTimePicker
          isVisible={isDatePickerVisible}
          mode="datetime"
          onConfirm={(date) => {
            setDueDate(date)
            setDatePickerVisibility(false)
          }}
          onCancel={() => {
            setDatePickerVisibility(false)
          }}
        />
        
        <View style={styles.baseContainer}>
          <TextInput
            onChangeText={(text) => {
              setDescription(text !== '' ? text : undefined)
            }}
            value={description}
            multiline
            placeholder={
              task.description !== undefined ? task.description : "No description"
            }
          />
        </View>

        <View
          style={{
            flex: 1,
            marginTop: 8,
          }}
        >
          <SubTasksScreen parent_id={task.id} />
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
    width: '100%',
    height: 48,
    flexDirection: 'row',
  },
  button: {
    height: '100%',
    aspectRatio: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  baseContainer: {
    minHeight: 48,
    marginVertical: 4,
    padding: 8,
    justifyContent: 'center',
  },
})