with open('c:/Users/user/OneDrive/Bureau/tripx/tripx/src/main/java/tn/esprit/controllers/admin/UserManagementController.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

stack = []
for i, line in enumerate(lines):
    for char in line:
        if char == '{':
            stack.append(i + 1)
        elif char == '}':
            if not stack:
                print(f"Extra closing brace at line {i + 1}")
            else:
                stack.pop()

if stack:
    print(f"Braces opened but not closed at lines: {stack}")
else:
    print("Braces are balanced")
