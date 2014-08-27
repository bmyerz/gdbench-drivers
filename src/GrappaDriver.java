import javax.management.RuntimeErrorException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Exception;
import java.lang.RuntimeException;
import java.lang.System;

public class GrappaDriver extends TestDriver {
  BufferedWriter person;
  BufferedWriter webpage;
  BufferedWriter like;
  BufferedWriter friend;
  long person_c;
  long webpage_c;
  long like_c;
  long friend_c;

  public GrappaDriver() {
    try {
      person = new BufferedWriter(new FileWriter("person",true));
      webpage = new BufferedWriter(new FileWriter("webpage",true));
      like = new BufferedWriter(new FileWriter("like",true));
      friend = new BufferedWriter(new FileWriter("friend",true));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //FIXME: only correct if first time
    person_c = 0;
    webpage_c = 0;
    like_c = 0;
    friend_c = 0;

  }

  public void printDBinfo() {
    System.out.println("TODO: printDBinfo");

  }

  @Override
  public boolean createDB(String dbname) {
    return true;
  }

  @Override
  public boolean openDB(String dbname) {
    return true;
  }

  @Override
  public boolean closeDB() {
    try {
      person.close();
      webpage.close();
      like.close();
      friend.close();
    } catch( IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  public boolean openTransaction() {
    return true;
  }

  @Override
  public boolean closeTransaction() {
    return true;
  }

  @Override
  public long getNumberOfNodes() {
    return person_c+webpage_c;
  }

  @Override
  public long getNumberOfEdges() {
    return friend_c+like_c;
  }

  @Override
  public long getDBsize() {
    return -1;
  }

  @Override
  public boolean insertPerson(long _pid, String _name, String _age, String _location) {
    try {
      person.write(String.format("%d, %s, %s, %s\n", _pid, _name, _age, _location));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    person_c++;
    return true;
  }

  @Override
  public boolean insertWebPage(long _wpid, String _url, String _creation) {
    try {
      webpage.write(String.format("%d, %s, %s\n", _wpid, _url, _creation));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    webpage_c++;
    return true;
  }

  @Override
  public boolean insertFriend(long id_person1, long id_person2) {
    try {
      friend.write(String.format("%d, %d\n", id_person1, id_person2));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    friend_c++;
    return true;
  }

  @Override
  public boolean insertLike(long id_person, long id_webpage) {
    try {
      like.write(String.format("%d, %d\n", id_person, id_webpage));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    like_c++;
    return true;
  }

  @Override
  public long Q1(String person_name) {
    return 0;
  }

  @Override
  public long Q2(long webpage_id) {
    return 0;
  }

  @Override
  public long Q3(long person_id) {
    return 0;
  }

  @Override
  public String Q4(long person_id) {
    return "";
  }

  @Override
  public long Q5(long person_id) {
    return 0;
  }

  @Override
  public long Q6(long person_id) {
    return 0;
  }

  @Override
  public long Q7(long person_id) {
    return 0;
  }

  @Override
  public boolean Q8(long person_id1, long person_id2) {
    return true;
  }

  @Override
  public long Q9(long person_id1, long person_id2) {
    return 0;
  }

  @Override
  public long Q10(long person_id1, long person_id2) {
    return 0;
  }

  @Override
  public long Q11(long person_id1, long person_id2) {
    return 0;
  }

  @Override
  public long Q12(long person_id1) {
    return 0;
  }
}
