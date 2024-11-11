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
import { useTaskContext } from "@/hooks/TaskContext";

export default function TaskDetailScreen() {
  const navigation = useNavigation<DetailNavigationProp>()
  const route = useRoute<DetailRouteProp>()
  const { taskJSON } = route.params
  const [task, _] = useState<Task>(JSON.parse(taskJSON) as Task)

  const { updateTask, deleteTask } = useTaskContext()

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
            style={{
              height: '100%',
              aspectRatio: 1,
              margin: 4,
              justifyContent: 'center',
            }}
            onPress={() => {
              navigation.goBack()
            }}
          >
            <MaterialIcons name="chevron-left" />
          </Pressable>

          {
            isEdited ? (
              <Pressable
                style={{
                  height: '100%',
                  aspectRatio: 1,
                  margin: 4,
                  alignItems: 'flex-end',
                  justifyContent: 'center',
                }}
                onPress={() => {
                  task.title = title
                  task.description = description
                  task.dueDate = dueDate

                  updateTask(task)

                  Keyboard.dismiss()
                }}
              >
                <MaterialIcons name="save" />
              </Pressable>
            ) : null
          }
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
            marginVertical: 8,
          }}
        >
          <SubTasksScreen parent_id={task.id} />
        </View>

        <Pressable
          style={{
            height: 48,
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: Colors.delete,
            borderRadius: 8,
          }}
          onPress={() => {
            deleteTask(task.id)

            navigation.goBack()
          }}
        >
          <Text>Delete</Text>
        </Pressable>
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
    alignContent: 'center',
    justifyContent: 'space-between',
  },
  baseContainer: {
    minHeight: 48,
    marginVertical: 4,
    padding: 8,
    justifyContent: 'center',
  },
})