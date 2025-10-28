# CryptoKit

##  Java Command-Line CryptoKit

A lightweight, command-line utility built in **Java** for performing common encoding, decoding, and encryption tasks.  
This tool is ideal for quick cryptographic operations and data transformations directly from your terminal.

---

##  Features

- **Encode:** Convert plain text into Base64, Hexadecimal, or Binary.  
- **Decode:** Convert Base64, Hexadecimal, or Binary back into plain text.  
- **XOR:** Encrypt or decrypt files using a repeating-key XOR cipher.

---

##  Setup & Installation

### 1. Requirements

- Java Development Kit (**JDK 17** or higher)

### 2. Compile the Code

Clone this repository or download `CryptoKit.java`.  
Then navigate to the project directory and compile the Java file:

```bash
javac CryptoKit.java
````

This will create a `CryptoKit.class` file in the same directory.

### 3. Run the Tool

Use the `java` command to run the tool.
All commands follow this basic structure:

```bash
java CryptoKit <command> [options]
```

---

##  Usage & Commands

###  Encode

Encodes a given string into a specified format.

**Command:** `encode`
**Options:**

* `--type <format>`: Format to encode to (`hex`, `b64`, `bin`)
* `--input <string>`: Input string to encode

**Example:**

```bash
# Encodes "hello world" into Base64
java CryptoKit encode --type b64 --input "hello world"
```

**Output:**

```
Encoding...
aGVsbG8gd29ybGQ=
```

---

###  Decode

Decodes a given string from a specified format back into plain text.

**Command:** `decode`
**Options:**

* `--type <format>`: Format to decode from (`hex`, `b64`, `bin`)
* `--input <string>`: Input string to decode

**Example (Base64):**

```bash
# Decodes "aGVsbG8gd29ybGQ=" from Base64
java CryptoKit decode --type b64 --input "aGVsbG8gd29ybGQ="
```

**Output:**

```
Decoding...
hello world
```

**Example (Hex):**

```bash
# Decodes "48656c6c6f" from Hex
java CryptoKit decode --type hex --input "48656c6c6f"
```

**Output:**

```
Decoding...
Hello
```

---

###  XOR Cipher

Encrypts or decrypts a file using a repeating-key XOR cipher.
This command reads a file and writes its output to a new file.

* If you provide a **plain-text** file (e.g., `message.txt`), it will be encrypted and saved as `message.txt.xor`.
* If you provide an **encrypted** file (e.g., `message.txt.xor`), it will be decrypted and saved as `message.txt`.

**Command:** `xor`
**Options:**

* `--key <key>`: Secret key for encryption/decryption
* `--file <filepath>`: Path to the file you want to process

#### Example (Encrypt)

Assume you have a file `secret.txt` containing:

```
This is a secret message.
```

```bash
# Encrypts secret.txt with the key "mykey"
java CryptoKit xor --key "mykey" --file "secret.txt"
```

**Output:**

```
XORing...
Operation successful. Output saved to: secret.txt.xor
```

(This creates a new file, `secret.txt.xor`, containing the encrypted and Base64-encoded ciphertext.)

#### Example (Decrypt)

```bash
# Decrypts secret.txt.xor with the same key "mykey"
java CryptoKit xor --key "mykey" --file "secret.txt.xor"
```

**Output:**

```
XORing...
Operation successful. Output saved to: secret.txt
```

(This decrypts the file and restores the original content.)

---
