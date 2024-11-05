import { useState } from "react";
import { useNavigation, useRoute } from "@react-navigation/native";
import { Button, Keyboard, Pressable, StyleSheet, Text, TextInput, TouchableWithoutFeedback, View } from "react-native";
import { SafeAreaView } from 'react-native-safe-area-context';
import { MaterialIcons } from "@expo/vector-icons";

import DateTimePicker from "react-native-modal-datetime-picker";

import { Task } from "@/components/TaskItem";

export default function TaskDetailScreen() {
  const navigation = useNavigation();
  const route = useRoute();
  const { taskJSON } = route.params;
  const [task, updateTask] = useState<Task>(JSON.parse(taskJSON) as Task);

  const [title, setTitle] = useState<string>(task.title);
  const [description, setDescription] = useState<string | undefined>(task.description !== undefined ? task.description : undefined);
  const [dueDate, setDueDate] = useState<Date | undefined>(task.dueDate !== undefined ? new Date(task.dueDate) : undefined);

  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  const isEdited = title !== task.title || description !== task.description || dueDate !== task.dueDate;
  
  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <SafeAreaView style={styles.container}>
        <View style={styles.topContainer}>
          <Pressable
            style={{ aspectRatio: 1, margin: 4 }}
            onPress={() => {
              navigation.navigate('Home', {
                updateTaskJSON: JSON.stringify(task)
              });
            }}
          >
            <MaterialIcons name="chevron-left" />
          </Pressable>

          {
            isEdited ? (
              <Pressable
                style={{ aspectRatio: 1, margin: 4 }}
                onPress={() => {
                  updateTask({
                    id: task.id,
                    title: title,
                    description: description,
                    dueDate: dueDate
                  })

                  Keyboard.dismiss();
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
            setDatePickerVisibility(true);
          }}
        >
          <MaterialIcons name="date-range" style={{ marginEnd: 4 }}/>
          <Text>{dueDate === undefined ? "Not setted" :dueDate.toISOString()}</Text>
        </Pressable>

        <DateTimePicker
          isVisible={isDatePickerVisible}
          mode="datetime"
          onConfirm={(date) => {
            setDueDate(date);
            setDatePickerVisibility(false);
          }}
          onCancel={() => {
            setDatePickerVisibility(false);
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

        <Button
          title="Delete"
          color='red'
          onPress={() => {
            navigation.navigate('Home', {
              deletedTaskId: task.id
            })
          }}
        />
      </SafeAreaView>
    </TouchableWithoutFeedback>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
  topContainer: {
    minHeight: 48,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between'
  },
  baseContainer: {
    minHeight: 48,
    marginVertical: 4,
    padding: 8,
    justifyContent: 'center',
  },
});