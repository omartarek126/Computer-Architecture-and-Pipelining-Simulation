# Computer Architecture and Pipelining Simulation

A simulation of a fictional processor design and architecture based on the Harvard architecture which is the digital computer architecture whose design is based on the concept where there are separate storage and separate buses (signal path) for instruction and data.

## Technologies Used:

- Java with an OOP approach
- Swing for the GUI

## Registers:

- 64 General-Purpose Registers (R0 to R63)
- 1 Status Register (stores the values of the flags)
- 1 Program Counter

## Instruction Set Architecture (ISA):

- Instruction Size: 16 bits
- Instruction Types: 2 (R-Format and I-Format)

![image](https://user-images.githubusercontent.com/61099815/175110922-c1866f9e-2c97-4b83-a766-83b9f3811655.png)
![image](https://user-images.githubusercontent.com/68354610/175104906-647def9a-7583-458b-ac2b-0cad6cddbfad.png)

## Flags:
- The Carry flag (C) indicates when an arithmetic carry or borrow has been generated out of the most significant bit position
  - Updated every ADD instruction
- The Two’s Complement Overflow flag (V) indicates when the result of a signed number operation is too large, causing the high-order bit to overflow into the sign bit.
  - Updated every ADD and SUB instruction
- The Negative flag (N) indicates a negative result in an arithmetic or logic operation
  - Updated every ADD, SUB, MUL, ANDI, EOR, SAL, and SAR instruction
- The Zero flag (Z) indicates that the result of an arithmetic or logical operation was zero
  - Updated every ADD, SUB, MUL, ANDI, EOR, SAL, and SAR instruction

## Datapath:

### 3 Stages:
- Instruction Fetch 
- Instruction Decode 
- Instruction Execute

### Pipelining:
- 3 instructions (maximum) running
- Control Hazards are handled using the flushing technique
