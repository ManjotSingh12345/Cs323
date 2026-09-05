import java.io.*;
import java.util.*;

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void printPerson(PrintWriter oFile) throws IOException {
        String describe = name + " is " + age + " years old.";
        //System.out.println(describe);
        oFile.println(describe);
    }
}

public class SinghM_Project0A_Main {

    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            System.out.println("Program needs 2 arguments");
            System.exit(1);
        }

        Scanner inFile = null;
        PrintWriter outFile = null;

        try {
            inFile = new Scanner(new FileReader(args[0]));
            System.out.println("inFile opened successfully");
        }
        catch(IOException e) {
            System.out.println("inFile cannot be opened");
            System.exit(1);
        }

        try {
            outFile = new PrintWriter(args[1]);
            System.out.println("outFile opened successfully");
        }
        catch(IOException e) {
            System.out.println("outFile cannot be opened");
            System.exit(1);
        }

        int numOfPeople = inFile.nextInt();
        System.out.println("*** There are " + numOfPeople + " people");
        Person[] people = new Person[numOfPeople];

        int index = 0;
        while (index < numOfPeople) {
            String name = inFile.next();
            int age = inFile.nextInt();
            Person p = new Person(name, age);
            people[index] = p;
            index++;
        }

        for(int i=0; i<numOfPeople; i++) {
            people[i].printPerson(outFile);
        }

        inFile.close();
        outFile.close();
    }
}