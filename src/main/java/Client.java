import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/*
Программа "Удаление пробелов"
В данной задаче я использую NonBlocking  взаимодействие клиента и сервера. Пользователь может например вводить текст частями, а получать
весь введеный им текст и предложения полностью, одним сообщением от сервера. Такой результат мы получили с помощью NonBlocking взаимодействия.
 */

public class Client {

    public static final int THREAD_SLEEP = 2000;

    public static void main(String[] args) throws IOException {
        // Определяем сокет сервера
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 23338);
        final SocketChannel socketChannel = SocketChannel.open();
        //  подключаемся к серверу
        socketChannel.connect(socketAddress);

        // Получаем входящий и исходящий потоки информации
        try (Scanner scanner = new Scanner(System.in)) {
            //  Определяем буфер для получения данных
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            String msg;
            String msg2;
            while (true) {
                System.out.println("Введите строку или текст для удаления пробелов: ");
                msg = scanner.nextLine();
                if ("end".equals(msg)) break;

                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));

                System.out.println("Введите продолжение строки или текста для удаления пробелов: ");
                msg2 = scanner.nextLine();
                if ("end".equals(msg)) break;

                socketChannel.write(ByteBuffer.wrap(msg2.getBytes(StandardCharsets.UTF_8)));

                Thread.sleep(THREAD_SLEEP);

                int bytesCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socketChannel.close();
        }
    }
}
