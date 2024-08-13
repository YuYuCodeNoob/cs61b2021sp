import os

# 获取当前目录
current_dir = os.getcwd()

# 遍历当前目录下的所有条目
for entry in os.scandir(current_dir):
    # 检查是否为目录且名称以"test"开头后跟数字
    if entry.is_dir() and entry.name.__contains__("test"):
        # 删除匹配的目录及其内容
        os.system(f'rm -rf "{entry.path}"')
        print(f"Deleted directory: {entry.path}")
