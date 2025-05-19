# 控制方向 2830 ai 对话接口文档

## 步骤
1. 请求chatId 
2. 根据chatId建立websocket连接进行ai对话

## 请求chatId
url: /aiofcontrol/2830/chat

方法：GET

参数： （int）taskId

return: (string) chatId

## websocket

url: /ai2830/server/assistant/{chatId}

### 发送websocket消息格式
```json
{
  "message":"",
  "course_id":"2830",
  "teacher_type":"assistant"
}
```

### 接收websocket消息格式
1. type=current(建立连接自动发送历史消息)
```json
{
    "type": "current",
    "message": null,
    "msgList": [
        {
            "course_id": "2830",
            "direction": "user",
            "id": 11,
            "message": "介绍一下 Python 基本语法",
            "timestamp": "2025-05-16T18:42:41",
            "user_id": "B5a0q2pAqdf8LISX"
        },
        {
            "course_id": "2830",
            "direction": "ai",
            "id": 12,
            "message": "Python 是一种高级编程语言，具有简单易学、可读性强的特点。以下是 Python 的基本语法要点：\n\n### 1. 变量与数据类型\nPython 是动态类型语言，不需要显式声明变量类型。\n\n```python\n# 整数\nx = 10\n\n# 浮点数\ny = 3.14\n\n# 字符串\nname = \"Python\"\n\n# 布尔值\nis_active = True\n```\n\n### 2. 控制结构\nPython 使用缩进来定义代码块。\n\n#### 条件语句\n```python\nif x > 5:\n    print(\"x is greater than 5\")\nelif x == 5:\n    print(\"x is equal to 5\")\nelse:\n    print(\"x is less than 5\")\n```\n\n#### 循环语句\n```python\n# for 循环\nfor i in range(5):\n    print(i)\n\n# while 循环\ncount = 0\nwhile count < 5:\n    print(count)\n    count += 1\n```\n\n### 3. 函数\n使用 `def` 关键字定义函数。\n\n```python\ndef greet(name):\n    return f\"Hello, {name}!\"\n\nprint(greet(\"Alice\"))\n```\n\n### 4. 数据结构\nPython 提供了多种内置数据结构。\n\n#### 列表\n```python\nfruits = [\"apple\", \"banana\", \"cherry\"]\nfruits.append(\"orange\")\nprint(fruits[0])  # 输出: apple\n```\n\n#### 元组\n```python\ncoordinates = (10.0, 20.0)\nprint(coordinates[1])  # 输出: 20.0\n```\n\n#### 字典\n```python\nperson = {\"name\": \"Alice\", \"age\": 25}\nprint(person[\"name\"])  # 输出: Alice\n```\n\n#### 集合\n```python\nunique_numbers = {1, 2, 3, 4}\nunique_numbers.add(5)\n```\n\n### 5. 类与对象\nPython 支持面向对象编程。\n\n```python\nclass Person:\n    def __init__(self, name, age):\n        self.name = name\n        self.age = age\n\n    def greet(self):\n        return f\"Hi, my name is {self.name}.\"\n\nperson = Person(\"Bob\", 30)\nprint(person.greet())\n```\n\n### 6. 异常处理\n使用 `try-except` 块捕获异常。\n\n```python\ntry:\n    result = 10 / 0\nexcept ZeroDivisionError:\n    print(\"Cannot divide by zero.\")\n```\n\n### 7. 模块与包\n可以导入模块和包来扩展功能。\n\n```python\nimport math\nprint(math.sqrt(16))  # 输出: 4.0\n```\n\n这些是 Python 的一些基本语法要点。您对哪个部分感兴趣或有疑问呢？我们可以进一步探讨具体的应用场景和示例。",
            "timestamp": "2025-05-16T18:43:09",
            "user_id": "B5a0q2pAqdf8LISX"
        }     
    ]
}
```

2. type 为 message-ack(返回user发送的消息) activity-start(开始流消息) stream(流消息) message(流消息结果) activity-stop(结束流消息)
```json
{
    "type": "current",
    "message": {
      "id": null,
      "user_id": null,
      "course_id": null,
      "teacher_type": null,
      "direction": "ai",
      "message": "好的！以下是基于 Python 实现的 **二分排序（Binary Insertion Sort）** 的代码。这种算法通过二分查找确定插入位置，从而减少了传统插入排序中的比较次数。\n\n---\n\n### **完整实现**\n\n```python\ndef binary_search(arr, val, start, end):\n    \"\"\"\n    使用二分查找找到插入位置\n    :param arr: 已排序的数组部分\n    :param val: 待插入的值\n    :param start: 搜索范围的起始索引\n    :param end: 搜索范围的结束索引\n    :return: 插入位置的索引\n    \"\"\"\n    while start <= end:\n        mid = (start + end) // 2\n        if arr[mid] < val:\n            start = mid + 1\n        else:\n            end = mid - 1\n    return start\n\ndef binary_insertion_sort(arr):\n    \"\"\"\n    二分排序函数\n    :param arr: 待排序的列表\n    :return: 排序后的列表\n    \"\"\"\n    n = len(arr)\n    for i in range(1, n):\n        # 当前需要插入的值\n        val = arr[i]\n        \n        # 使用二分查找找到插入位置\n        insert_pos = binary_search(arr, val, 0, i - 1)\n        \n        # 将元素向后移动，腾出插入位置\n        j = i\n        while j > insert_pos:\n            arr[j] = arr[j - 1]\n            j -= 1\n        \n        # 插入当前值\n        arr[insert_pos] = val\n    \n    return arr\n\n# 测试代码\nif __name__ == \"__main__\":\n    unsorted_array = [34, 7, 23, 32, 5, 62, 32, 2]\n    sorted_array = binary_insertion_sort(unsorted_array)\n    print(f\"排序结果: {sorted_array}\")\n```\n\n---\n\n### **运行结果**\n假设输入数组为 `[34, 7, 23, 32, 5, 62, 32, 2]`，运行程序后输出如下：\n\n```\n排序结果: [2, 5, 7, 23, 32, 32, 34, 62]\n```\n\n---\n\n### **代码逻辑解析**\n1. **`binary_search` 函数**：\n   - 这是一个辅助函数，用于在已排序的部分数组中找到待插入值的正确位置。\n   - 使用二分查找将比较次数从 $O(n)$ 降低到 $O(\\log n)$。\n\n2. **`binary_insertion_sort` 函数**：\n   - 遍历数组中的每个元素，从第二个元素开始（索引为1）。\n   - 对于每个元素，调用 `binary_search` 找到其插入位置。\n   - 将插入位置之后的元素依次向后移动，腾出空间，然后插入当前值。\n\n3. **时间复杂度**：\n   - 比较操作：$O(n \\log n)$（因为每次插入时使用二分查找）。\n   - 元素移动：$O(n^2)$（因为需要移动元素腾出空间）。\n\n---\n\n### **特点**\n- **优点**：\n  - 比传统插入排序更高效，减少了比较次数。\n  - 适合处理小规模或部分有序的数据。\n\n- **缺点**：\n  - 元素移动仍然需要 $O(n^2)$ 的时间复杂度，对于大规模数据性能有限。\n\n如果你还有其他需求（例如优化、扩展功能或更复杂的场景），可以随时告诉我！ 😊",
      "timestamp": "2025-05-17T14:24:24.7916704"
    },
    "msgList": null
}
```