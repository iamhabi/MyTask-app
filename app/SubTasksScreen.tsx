import { useState } from "react";
import { FlatList, StyleSheet, View } from "react-native";
import { useAppNavigation } from "./_layout";

import TaskInputField from '@/components/TaskInputField';
import TaskItem from "@/components/TaskItem";

import { Task } from "@/types/task";

export default function SubTasksScreen() {
  const navigation = useAppNavigation()
  const [subTasks, updateSubTasks] = useState(Array<Task>);

  return (
    <View style={styles.container}>
      <FlatList
        style={{
          flex: 1,
          marginStart: 16,
        }}
        data={subTasks}
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onClick={(task) => {
              navigation.push('Detail', {
                taskJSON: JSON.stringify(task)
              })
            }}
            onDelete={(deletedTask) => {
              
            }}
          />
        }
      />
      <TaskInputField
        onAdded={(task) => {
          updateSubTasks([...subTasks, task])
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  }
});