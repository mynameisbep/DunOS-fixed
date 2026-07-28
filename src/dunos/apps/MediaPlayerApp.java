package dunos.apps;

import dunos.ui.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * DunDunDunOS MediaPlayer - Audio/video player with play controls.
 */
public class MediaPlayerApp {

    private JLabel songTitle;
    private JLabel timeLabel;
    private JSlider progressSlider;
    private JSlider volumeSlider;
    private boolean playing;
    private Timer progressTimer;
    private int currentProgress;

    public Window createWindow() {
        Window window = new Window("Media Player", "mediaplayer", null);
        window.setSize(400, 350);
        window.setResizable(false);
        window.setMaximizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(28, 28, 28));

        // Album art / visualizer placeholder
        JPanel visualizer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (playing) {
                    // Simple visualizer bars
                    int bars = 20;
                    int barW = getWidth() / (bars + 1);
                    for (int i = 0; i < bars; i++) {
                        int h = (int)(Math.random() * getHeight() * 0.7 + getHeight() * 0.1);
                        g2d.setColor(new Color(0, 120, 212, 150));
                        g2d.fillRoundRect(i * (barW + 1) + 5, getHeight() - h, barW, h, 3, 3);
                    }
                }
                g2d.dispose();
            }
        };
        visualizer.setPreferredSize(new Dimension(0, 120));
        visualizer.setBackground(new Color(30, 30, 40));
        content.add(visualizer, BorderLayout.NORTH);

        // Song info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        songTitle = new JLabel("🎵 No track loaded");
        songTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songTitle.setForeground(new Color(220, 220, 220));

        JLabel artistLabel = new JLabel("DunOS Media Player");
        artistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        artistLabel.setForeground(new Color(140, 140, 140));

        infoPanel.add(songTitle, BorderLayout.NORTH);
        infoPanel.add(artistLabel, BorderLayout.CENTER);

        // Progress
        JPanel progressPanel = new JPanel(new BorderLayout(6, 0));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(new EmptyBorder(0, 10, 5, 10));

        timeLabel = new JLabel("0:00 / 0:00");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(160, 160, 160));
        progressPanel.add(timeLabel, BorderLayout.WEST);

        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setOpaque(false);
        progressPanel.add(progressSlider, BorderLayout.CENTER);

        content.add(infoPanel, BorderLayout.CENTER);
        content.add(progressPanel, BorderLayout.SOUTH);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        controls.setOpaque(false);

        JButton prevBtn = createControlBtn("⏮");
        JButton playBtn = createControlBtn("▶");
        JButton nextBtn = createControlBtn("⏭");
        JButton stopBtn = createControlBtn("⏹");
        JButton openBtn = createControlBtn("📂");

        playBtn.addActionListener(e -> {
            playing = !playing;
            playBtn.setText(playing ? "⏸" : "▶");
            if (playing) {
                songTitle.setText("🎵 Playing - Sample Track");
                startProgress();
            } else {
                stopProgress();
            }
        });

        stopBtn.addActionListener(e -> {
            playing = false;
            playBtn.setText("▶");
            currentProgress = 0;
            progressSlider.setValue(0);
            timeLabel.setText("0:00 / 0:00");
            stopProgress();
        });

        openBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("Music");
            if (chooser.showOpenDialog(content) == JFileChooser.APPROVE_OPTION) {
                songTitle.setText("🎵 " + chooser.getSelectedFile().getName());
                playing = true;
                playBtn.setText("⏸");
                startProgress();
            }
        });

        controls.add(openBtn);
        controls.add(prevBtn);
        controls.add(playBtn);
        controls.add(nextBtn);
        controls.add(stopBtn);

        // Volume
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(4, 10, 8, 10));

        JLabel volLabel = new JLabel("🔊");
        volLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        volLabel.setForeground(new Color(160, 160, 160));

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(100, 20));

        bottomPanel.add(volLabel, BorderLayout.WEST);
        bottomPanel.add(volumeSlider, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(controls, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.CENTER);
        content.add(southPanel, BorderLayout.SOUTH);

        return window;
    }

    private JButton createControlBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(new Color(45, 45, 45));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void startProgress() {
        if (progressTimer != null) progressTimer.stop();
        progressTimer = new Timer(500, e -> {
            currentProgress += 2;
            if (currentProgress > 100) currentProgress = 0;
            progressSlider.setValue(currentProgress);
            int totalSec = 180;
            int currentSec = (int)(currentProgress / 100.0 * totalSec);
            timeLabel.setText(String.format("%d:%02d / %d:%02d", 
                currentSec / 60, currentSec % 60, totalSec / 60, totalSec % 60));
        });
        progressTimer.start();
    }

    private void stopProgress() {
        if (progressTimer != null) {
            progressTimer.stop();
            progressTimer = null;
        }
    }
}

