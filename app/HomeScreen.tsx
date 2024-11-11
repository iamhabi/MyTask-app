import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem from '@/components/TaskItem';
import { useAppNavigation } from '@/types/navigation';
import { ROUTES } from '@/constants/routes';
import { useTaskContext } from '@/hooks/TaskContext';

export default function HomeScreen() {
  const navigation = useAppNavigation()
  
  const { tasks, addTask, deleteTask } = useTaskContext()

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
        data={
          tasks.filter(task => task.parent_id === undefined)
        }
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onClick={(task) => {
              navigation.navigate(ROUTES.DETAIL, {
                taskJSON: JSON.stringify(task)
              })
            }}
            onDelete={(deletedTask) => {
              deleteTask(deletedTask.id)
            }}
          />
        }
      />
      <TaskInputField
        onCreate={(title, description, dueDate) => {
          addTask(undefined, title, description, dueDate)
        }}
      />
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
})