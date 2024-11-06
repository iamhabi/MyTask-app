import { useState } from "react";
import { StyleSheet, TextInput, View, Text, Pressable } from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import DateTimePicker from "react-native-modal-datetime-picker";

import IconButton from "./IconButton";
import { Task } from "./TaskItem";

type Props = {
  onAdded: (task: Task) => void;
}

export default function TaskInputField({ onAdded: onTaskAdded }: Props) {
  const [title, setTitle] = useState('');
  const [dueDate, setDueDate] = useState<Date | undefined>(undefined);
  const [description, setDescription] = useState<string | undefined>(undefined);

  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
  const [isDescriptionVisible, setDescriptionVisibility] = useState(false);

  return (
    <View style={styles.container}>
      <View style={{ flexDirection: 'row' }}>
        <Pressable onPress={() => {
          setDatePickerVisibility(true);
        }}>
          <View style={styles.smallContainer}>
            <MaterialIcons name="date-range" style={{ marginEnd: 4 }}/>
            <Text>{dueDate === undefined ? "Due date" :dueDate.toISOString()}</Text>
          </View>
        
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
        </Pressable>
        <Pressable style={styles.smallContainer} onPress={() => {
          setDescriptionVisibility(!isDescriptionVisible);
        }}>
          <MaterialIcons name="short-text" style={{ marginEnd: 4 }}/>
          <Text>Description</Text>
        </Pressable>
      </View>

      <View style={styles.newTaskContainer}>
        <TextInput
          style={{ flex: 1 }}
          onChangeText={setTitle}
          value={title}
          numberOfLines={1}
          placeholder="New task"
        />

        <IconButton
          icon="add"
          size={48}
          onPress={() => {
            if (title === '') {
              alert('Please input task');

              return;
            }

            onTaskAdded({id: Math.floor(Math.random() * 256), title: title, description: description, dueDate: dueDate});

            setTitle('');
            setDescription(undefined);
            setDueDate(undefined);
            setDescriptionVisibility(false);
          }}
        />
      </View>

      {
        isDescriptionVisible ? (
          <TextInput
            style={{ minHeight: 48, }}
            onChangeText={(text) => {
              setDescription(text !== '' ? text : undefined)
            }}
            value={description}
            multiline
            numberOfLines={3}
            placeholder="Description"
            autoFocus={isDescriptionVisible}
          />
        ) : null
      }
      
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 8,
    borderWidth: 1,
    borderRadius: 16,
  },
  newTaskContainer: {
    height: 64,
    flexDirection: 'row',
    alignItems: 'center'
  },
  smallContainer: {
    flexDirection: 'row',
    paddingHorizontal: 8,
    marginEnd: 8,
    borderWidth: 0.7,
    borderRadius: 24,
    alignItems: 'center'
  }
});