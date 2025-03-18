import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class Main {

    static class Vertex{

        float x, y, z;

        Vertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        static Vertex Rotate(Vertex v, Angle a) {
            float x = v.x;
            float y = v.y;
            float z = v.z;

            float rotatedX = x * (float) Math.cos(a.y) - z * (float) Math.sin(a.y);
            float rotatedZ = z * (float) Math.cos(a.y) + x * (float) Math.sin(a.y);

            float rotatedY = y * (float) Math.cos(a.x) - rotatedZ * (float) Math.sin(a.x);
            rotatedZ = rotatedZ * (float) Math.cos(a.x) + y * (float) Math.sin(a.x);

            float finalX = rotatedX * (float) Math.cos(a.z) - rotatedY * (float) Math.sin(a.z);
            float finalY = rotatedX * (float) Math.sin(a.z) + rotatedY * (float) Math.cos(a.z);
            float finalZ = rotatedZ;
            return new Vertex(finalX, finalY, finalZ);
        }

        static float Distance(Vertex v1, Vertex v2){
            Vertex dif = new Vertex(v1.x-v2.x, v1.y-v2.y, v1.z-v2.z);
            return (float)Math.sqrt(dif.x*dif.x+dif.y*dif.y+dif.z*dif.z);
        }
    }

    static class Polygon {

        int p1, p2, p3;
        char ch;

        Polygon(int p1, int p2, int p3, char ch) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.ch = ch;
        }
    }

    static class Angle {

        float x, y, z;

        Angle(float x, float y) {
            this.x = x;
            this.y = y;
        }

        Angle(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static int width = 150, height = 90;
    static int frames = 0;
    static boolean lineMode = false;
    static float avg1 = 0, avg2 = 0, avg3 = 0;
    static Object lockObj = new Object();
    static float[][] zMap = new float[height][width];

    public static void main(String[] args) throws InterruptedException {

        var result = LoadObjectFromFile("D:\\projects\\dr\\3DASCII\\cube\\cube.txt"); // Укажите свой файл
        List<Vertex> vertices = result.vertices;
        List<Polygon> polygons = result.connections;

        Window window = new Window();
        Angle angle = new Angle(0, 0, 0);
        Vertex position = new Vertex(0, 0, 5);

        float angSpeed = 0.7f, speed = 0.4f;
        window.textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        angle.y += Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_DOWN -> {
                        angle.y -= Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_LEFT -> {
                        angle.x -= Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        angle.x += Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_Q -> {
                        angle.z += Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_E -> {
                        angle.z -= Math.PI / 180 * angSpeed;
                    }
                    case KeyEvent.VK_W -> {
                        position.x += speed;
                    }
                    case KeyEvent.VK_S -> {
                        position.x -= speed;
                    }
                    case KeyEvent.VK_A -> {
                        position.z -= speed;
                    }
                    case KeyEvent.VK_D -> {
                        position.z += speed;
                    }
                    case KeyEvent.VK_C -> {
                        position.y -= speed;
                    }
                    case KeyEvent.VK_Z -> {
                        position.y += speed;
                    }
                    
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F ->{
                        synchronized (lockObj) {
                        AddVertex(vertices, polygons, position, angle, 2);
                        }
                    }
                    case KeyEvent.VK_R ->{
                        lineMode = !lineMode;
                    }
                }
            }
        });
        window.setVisible(true);
        window.textArea.requestFocus();

        try {
            while (true) {
//            var frame = Draw(vertices, connections, new Angle(
//                    Math.PI / 2 * Math.sin(i * Math.PI / 100),
//                    Math.PI / 100 * i
//            ));
                var arr1 = vertices.toArray(new Vertex[0]);
                var arr2 = polygons.toArray(new Polygon[0]);
                var frame = Draw(arr1, arr2, angle, position);
                window.UpdateFrame(frame);
                window.UpdateInfo(position, angle, arr1, arr2);
            }
        } catch (Exception e) {
            System.out.println("wdad");
        }
    }

    static class Result {

        List<Vertex> vertices;
        List<Polygon> connections;

        Result(List<Vertex> v, List<Polygon> c) {
            vertices = v;
            connections = c;
        }
    }

    static Result LoadObjectFromFile(String filePath) {
        List<Vertex> vertices = new ArrayList<>();
        List<Polygon> connections = new ArrayList<>();
        String currentSection = "";

        try {
            for (String line : Files.readAllLines(Paths.get(filePath))) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("[") && trimmedLine.endsWith("]")) {
                    currentSection = trimmedLine.substring(1, trimmedLine.length() - 1);
                    continue;
                }

                if (trimmedLine.isEmpty()) {
                    continue;
                }

                switch (currentSection) {
                    case "Vertices" -> {
                        String[] vParts = trimmedLine.split(",");
                        vertices.add(new Vertex(
                                Float.parseFloat(vParts[0]),
                                Float.parseFloat(vParts[1]),
                                Float.parseFloat(vParts[2])
                        ));
                    }
                    case "Polygons" -> {
                        String[] cParts = trimmedLine.split(",");
                        connections.add(new Polygon(
                                Integer.parseInt(cParts[0]),
                                Integer.parseInt(cParts[1]),
                                Integer.parseInt(cParts[2]),
                                cParts[3].charAt(0)
                        ));
                        break;
                    }

                }
            }
        } catch (Exception e) {

        }

        return new Result(
                vertices,
                connections
        );
    }

    static String Draw(Vertex[] vertices, Polygon[] connections, Angle angle, Vertex offset) {
        Vertex[] projectedVertices = new Vertex[vertices.length];
        long t = System.currentTimeMillis();

        for (int i = 0; i < vertices.length; i++) {
            Vertex v = vertices[i];

            var r = Vertex.Rotate(v, angle);
            float scale = 170 / (r.z + offset.z);
            float projectedX = r.x * scale + 45;
            float projectedY = r.y * scale + 70;
            projectedVertices[i] = new Vertex(0, 0, 0);
            projectedVertices[i].x = projectedX + offset.x;
            projectedVertices[i].y = projectedY + offset.y;
            projectedVertices[i].z = r.z + offset.z;
        }

        avg1 = (avg1 * frames + (System.currentTimeMillis() - t)) / (frames + 1);
        t = System.currentTimeMillis();

        char[][] screen = new char[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                screen[j][i] = ' ';
                zMap[j][i] = Float.MAX_VALUE;
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(connections.length);

        for (Polygon p : connections) {
            executor.submit(() -> {
                if (lineMode){
                    DrawLine(screen, projectedVertices[p.p1].x,projectedVertices[p.p1].y,
                    projectedVertices[p.p2].x,projectedVertices[p.p2].y, p.ch);
                    DrawLine(screen, projectedVertices[p.p3].x,projectedVertices[p.p3].y,
                    projectedVertices[p.p2].x,projectedVertices[p.p2].y, p.ch);
                    DrawLine(screen, projectedVertices[p.p1].x,projectedVertices[p.p1].y,
                    projectedVertices[p.p3].x,projectedVertices[p.p3].y, p.ch);
                }
                else FillTriangle(screen,
                        projectedVertices[p.p1],
                        projectedVertices[p.p2],
                        projectedVertices[p.p3],
                        p.ch
                );
                latch.countDown();
            });
        }

        executor.shutdown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder render = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                render.append(screen[i][j]).append(screen[i][j]);
            }
            render.append("\n");
        }

        avg2 = (avg2 * frames + (System.currentTimeMillis() - t)) / (frames + 1);
        t = System.currentTimeMillis();
        render.append("\n").append(avg1).append(" ").append(avg2).append(" ").append(avg3);
        avg3 = (avg3 * frames + (System.currentTimeMillis() - t)) / (frames + 1);
        frames++;
        return render.toString();
    }

    static void DrawLine(char[][] screen, float x0d, float y0d, float x1d, float y1d, char ch) {
        int x0 = (int) x0d;
        int y0 = (int) y0d;
        int x1 = (int) x1d;
        int y1 = (int) y1d;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x0 >= 0 && x0 < height && y0 >= 0 && y0 < width) {
                synchronized (lockObj) {
                    screen[x0][y0] = ch;
                }
            }
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    static void FillTriangle(char[][] screen, Vertex p1, Vertex p2, Vertex p3, char ch) {
        // Определяем ограничивающий прямоугольник треугольника
        int minX = (int) Math.max(Math.min(Math.min(p1.x, p2.x), p3.x), 0);
        int maxX = (int) Math.min(Math.max(Math.max(p1.x, p2.x), p3.x), height - 1);
        int minY = (int) Math.max(Math.min(Math.min(p1.y, p2.y), p3.y), 0);
        int maxY = (int) Math.min(Math.max(Math.max(p1.y, p2.y), p3.y), width - 1);

        // Перебираем все точки в ограничивающем прямоугольнике
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (IsPointInsideTriangle(x, y, p1, p2, p3)) {
                    float z = 0;
                    float x1 = p1.x, y1 = p1.y;
                    float x2 = p2.x, y2 = p2.y;
                    float x3 = p3.x, y3 = p3.y;

                    float denominator = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

                    float l1, l2, l3;
                    if (denominator == 0) {
                        z = (p1.z + p2.z + p3.z) / 3;
                    } else {
                        l1 = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator;
                        l2 = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator;
                        l3 = 1.0f - l1 - l2;

                        z = l1 * p1.z + l2 * p2.z + l3 * p3.z;
                    }
                    if (zMap[x][y] < z || z <= 0) {
                        continue;
                    }
                    synchronized (lockObj) {
                        zMap[x][y] = z;
                        screen[x][y] = ch;
                    }
                }
            }
        }
    }

    static boolean IsPointInsideTriangle(float x, float y, Vertex a, Vertex b, Vertex c) {
        // Вычисляем векторные произведения для проверки положения точки относительно ребер
        float crossAB = (b.x - a.x) * (y - a.y) - (b.y - a.y) * (x - a.x);
        float crossBC = (c.x - b.x) * (y - b.y) - (c.y - b.y) * (x - b.x);
        float crossCA = (a.x - c.x) * (y - c.y) - (a.y - c.y) * (x - c.x);

        // Проверяем, находятся ли все произведения с одной стороны (включая границы)
        boolean allNonNegative = (crossAB >= 0) && (crossBC >= 0) && (crossCA >= 0);
        boolean allNonPositive = (crossAB <= 0) && (crossBC <= 0) && (crossCA <= 0);

        return allNonNegative || allNonPositive;
    }

    static void AddVertex(List<Vertex> vertices, List<Polygon> polygons, Vertex position, Angle rotation, float distance){
        Vertex newOne = Vertex.Rotate(new Vertex(0, 0, distance), new Angle(rotation.x, rotation.y, rotation.z));
        newOne = new Vertex(newOne.x-position.x, newOne.y-position.y, newOne.z-position.z);
        float min1 = -2, min2 = -2;
        int point1 = 0, point2 = 0;
        for (int i = 0; i < vertices.size(); i++) {
            float dist = Vertex.Distance(vertices.get(i), newOne);
            if (dist<min2||min2<0){
                min2 = dist;
                point2 = i;
            }
            else if ((dist<min1|| min1<0)&&i!=point2){
                min1 = dist;
                point1 = i;
            }
        }
        vertices.add(newOne);
        polygons.add(new Polygon(vertices.size()-1, point1, point2, String.valueOf(polygons.size()).charAt(0)));
    }
}
