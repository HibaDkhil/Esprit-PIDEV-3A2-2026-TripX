import sys

def check_braces(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    stack = []
    line_num = 1
    col_num = 0
    
    in_string = False
    in_char = False
    in_comment = False
    in_multiline_comment = False
    
    for i, char in enumerate(content):
        if char == '\n':
            line_num += 1
            col_num = 0
            if in_comment:
                in_comment = False
            continue
        
        col_num += 1
        
        if in_comment:
            continue
        
        if in_multiline_comment:
            if char == '*' and i + 1 < len(content) and content[i+1] == '/':
                in_multiline_comment = False
                # skip the /
            continue
            
        if in_string:
            if char == '"' and content[i-1] != '\\':
                in_string = False
            continue
            
        if in_char:
            if char == "'" and content[i-1] != '\\':
                in_char = False
            continue
            
        if char == '/' and i + 1 < len(content):
            if content[i+1] == '/':
                in_comment = True
                continue
            if content[i+1] == '*':
                in_multiline_comment = True
                continue
                
        if char == '"':
            in_string = True
            continue
        if char == "'":
            in_char = True
            continue
            
        if char == '{':
            stack.append(('{', line_num, col_num))
        elif char == '}':
            if not stack:
                print(f"Extra closing brace at line {line_num}, col {col_num}")
                return
            stack.pop()
            
    if stack:
        for b, l, c in stack:
            print(f"Unclosed brace '{b}' from line {l}, col {c}")
    else:
        print("Braces are balanced!")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        check_braces(sys.argv[1])
