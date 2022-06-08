DROP TABLE TEST;

CREATE TABLE TEST (
    ID NUMBER(10) NOT NULL,
    USER_EMAIL VARCHAR2(20) NOT NULL,
    Q_ID VARCHAR2(8) NOT NULL,
    NR_ORDINE NUMBER(1) NOT NULL,
    RASPUNS VARCHAR2(20) NULL,
    RASPUNS_CORECT VARCHAR2(100) NOT NULL,
    ID_RASPUNS_1 VARCHAR2(8) NOT NULL,
    ID_RASPUNS_2 VARCHAR2(8) NOT NULL,
    ID_RASPUNS_3 VARCHAR2(8) NOT NULL,
    ID_RASPUNS_4 VARCHAR2(8) NOT NULL,
    ID_RASPUNS_5 VARCHAR2(8) NOT NULL,
    ID_RASPUNS_6 VARCHAR2(8) NOT NULL,
    FINISHED VARCHAR(1) DEFAULT '0'
)
/
ALTER TABLE TEST ADD (CONSTRAINT test_pk PRIMARY KEY (ID));

DROP SEQUENCE test_seq;

CREATE SEQUENCE test_seq START WITH 1;


--- TRIGER PENTRU AUTOINCREMENT ID
CREATE OR REPLACE TRIGGER AUTOINCREMENT_TEST 
BEFORE INSERT ON TEST 
FOR EACH ROW
BEGIN
    SELECT test_seq.NEXTVAL INTO :new.id FROM dual;
END;

CREATE OR REPLACE FUNCTION CREATE_TEST(user_email IN VARCHAR2) RETURN VARCHAR2 AS
    question_id INTREBARI.ID%TYPE;
    first_q_id INTREBARI.ID%TYPE;
    
    found_correct_ans BOOLEAN := FALSE;
    first_q BOOLEAN := FALSE;
    contor INTEGER := 0;
    no_order INTEGER := 0;
    ids VARCHAR2(100) := '';
    corect_ans VARCHAR2(100) := '';

    v_cursor_id INTEGER;
    v_ok INTEGER;
BEGIN
    FOR obj IN (SELECT * FROM (SELECT * FROM DOMENII ORDER BY DBMS_RANDOM.RANDOM) WHERE ROWNUM <= 10) LOOP
        
        SELECT ID INTO question_id FROM (SELECT ID FROM INTREBARI WHERE DOMENIU = obj.D_ID ORDER BY DBMS_RANDOM.RANDOM) WHERE ROWNUM = 1;
        ids := '';
        corect_ans := '''';
        contor := 0;
        found_correct_ans := FALSE;
        FOR ans IN (SELECT * FROM RASPUNSURI WHERE Q_ID = question_id ORDER BY DBMS_RANDOM.RANDOM) LOOP
            IF ans.CORECT = '1' THEN
                found_correct_ans := TRUE;
            END IF;
            
            IF found_correct_ans = TRUE OR contor < 5 THEN
                contor := contor + 1;
                ids := ids || '''' || ans.ID || '''';
                
                IF contor < 6 THEN
                    ids := ids || ', ';
                END IF;
                
                IF ans.CORECT = '1' THEN
                    corect_ans := corect_ans || ans.ID || ',';
                END IF;
            END IF;
            
            IF contor = 6 THEN 
                EXIT;
            END IF;
            
        END LOOP;
        
        corect_ans := SUBSTR(corect_ans, 1, LENGTH(corect_ans) - 1) || '''';

        v_cursor_id := DBMS_SQL.OPEN_CURSOR;
        DBMS_SQL.PARSE(v_cursor_id, 
        'INSERT INTO TEST (USER_EMAIL, Q_ID, RASPUNS, NR_ORDINE, RASPUNS_CORECT, ID_RASPUNS_1, ID_RASPUNS_2, ID_RASPUNS_3, ID_RASPUNS_4, ID_RASPUNS_5, ID_RASPUNS_6) VALUES (' 
        || '''' || user_email || '''' || ', '
        || '''' || question_id || '''' || ', ' 
        || 'NULL, ' 
        || no_order || ', '
        || corect_ans || ', '
        || ids || ')', DBMS_SQL.NATIVE);
        
        no_order := no_order + 1;
        v_ok := DBMS_SQL.EXECUTE(v_cursor_id);
        DBMS_SQL.CLOSE_CURSOR(v_cursor_id);
        
        IF first_q = FALSE THEN
            SELECT test_seq.currval INTO first_q_id FROM DUAL;
            first_q := TRUE;
        END IF;
    END LOOP;
    
    RETURN first_q_id;
END;
/


CREATE OR REPLACE FUNCTION URMATOAREA_INTREBARE(email IN VARCHAR2, raspuns IN VARCHAR2 DEFAULT NULL) RETURN NUMBER AS
    first_q VARCHAR2(8);
    next_q NUMBER(10);
    v_cursor_id INTEGER;
    v_ok INTEGER;
    ord NUMBER(1);
BEGIN
    IF raspuns IS NULL THEN
        first_q := CREATE_TEST(email);
        RETURN first_q;
    END IF;
    
    --- id intrebare curenta
    SELECT ID, NR_ORDINE INTO next_q, ord FROM TEST WHERE RASPUNS IS NULL AND USER_EMAIL = email AND ROWNUM = 1 ORDER BY NR_ORDINE;

    v_cursor_id := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(v_cursor_id, 'UPDATE TEST SET RASPUNS = ' || '''' || raspuns || '''' || 'WHERE ID = ' || '''' || next_q || '''' , DBMS_SQL.NATIVE);
    v_ok := DBMS_SQL.EXECUTE(v_cursor_id);
    DBMS_SQL.CLOSE_CURSOR(v_cursor_id);

    
    IF ord < 9 THEN
        --- id urmatoarea intrebare
        SELECT ID INTO next_q FROM TEST WHERE RASPUNS IS NULL AND USER_EMAIL = email AND ROWNUM = 1 ORDER BY NR_ORDINE;
        RETURN next_q; 
            
    END IF;
    
    RETURN -1;
END;
/

CREATE OR REPLACE FUNCTION PUNCTAJ(email IN VARCHAR2) RETURN NUMBER AS
    punctaj_total NUMBER := 0;
    punctaj NUMBER;
    nr_corecte NUMBER;
    nr_ghicite NUMBER;
    nr_gresite NUMBER;
    
    ind INTEGER;
    ans VARCHAR2(8);
BEGIN
    FOR obj IN (SELECT * FROM TEST WHERE USER_EMAIL = email AND FINISHED = '0') LOOP
        nr_corecte := REGEXP_COUNT(obj.RASPUNS_CORECT, ',') + 1;
        punctaj := 10 / nr_corecte;
        nr_ghicite := 0;
        nr_gresite := 0;
        LOOP     
            ind := INSTR(obj.RASPUNS, ',');
            IF ind > 0 THEN
                ans := SUBSTR(obj.RASPUNS, 1, ind - 1);
                obj.RASPUNS := SUBSTR(obj.RASPUNS, ind + 1);
                IF REGEXP_COUNT(obj.RASPUNS_CORECT, ans) = 0 THEN
                    nr_gresite := nr_gresite + 1;
                ELSE     
                    nr_ghicite := nr_ghicite + 1;
                END IF;
            ELSE
                ans := obj.RASPUNS;
                IF REGEXP_COUNT(obj.RASPUNS_CORECT, ans) = 0 THEN
                    nr_gresite := nr_gresite + 1;
                ELSE     
                    nr_ghicite := nr_ghicite + 1;
                END IF;
                EXIT;
            END IF;
        END LOOP;
        
        IF (nr_ghicite * punctaj - nr_gresite * punctaj) > 0 THEN
            punctaj_total := punctaj_total + (nr_ghicite * punctaj - nr_gresite * punctaj);
        END IF;
    END LOOP;
    
    UPDATE TEST SET FINISHED = '1' WHERE USER_EMAIL = email;
    
    RETURN punctaj_total;
END;
/

