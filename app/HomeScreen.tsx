import { useState } from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem, { Task } from '@/components/TaskItem';

export default function HomeScreen() {
  const [tasks, updateTasks] = useState(Array<Task>)

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
        data={tasks}
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onDelete={(deletedTask) => {
              updateTasks(
                tasks.filter(task =>
                  task.id !== deletedTask.id
                )
              );
            }}
          />
        }
      />
      <TaskInputField
        onAdded={(task) => {
          updateTasks([...tasks, task]);
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
})