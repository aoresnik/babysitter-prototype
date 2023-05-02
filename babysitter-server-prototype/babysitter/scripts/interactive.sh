#! /bin/bash

echo "Type any text - it will be echoed back to you."
echo "Type 'exit' to exit."

while true; do
    read -r -p "> " input
    if [ "$input" == "exit" ]; then
        break
    fi
    echo "$input"
done

