export interface Task {
  id: number;
  title: string;
  description: string | undefined;
  dueDate: Date | undefined;
};
