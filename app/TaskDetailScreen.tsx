import { useState } from "react";
import { useRoute } from "@react-navigation/native";
import { Button, Keyboard, Pressable, StyleSheet, Text, TextInput, TouchableWithoutFeedback, View } from "react-native";
import { SafeAreaView } from 'react-native-safe-area-context';
import { MaterialIcons } from "@expo/vector-icons";

import DateTimePicker from "react-native-modal-datetime-picker";

export default function TaskDetailScreen() {
  const route = useRoute();
  const { taskJSON } = route.params;
  const task = JSON.parse(taskJSON);

  const [title, setTitle] = useState<string>(task.title);
  const [description, setDescription] = useState<string | undefined>(task.description !== undefined ? task.description : undefined);
  const [dueDate, setDueDate] = useState<Date | undefined>(task.dueDate !== undefined ? new Date(task.dueDate) : undefined);

  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);
  
  return (
    <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
      <SafeAreaView style={styles.container}>
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
            onChangeText={setDescription}
            value={description}
            placeholder={
              task.description !== undefined ? task.description : "No description"
            }
          />
        </View>

        <Button
          title="Delete"
          color='red'
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
  baseContainer: {
    minHeight: 48,
    marginVertical: 4,
    padding: 8,
    justifyContent: 'center'
  },
});