package com.chcorp.homes.notice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class NoticeAdminButtonFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/admin".equals(request.getRequestURI())
                || !"notice".equals(request.getParameter("section"))
                || !"GET".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        BufferedResponseWrapper wrapper = new BufferedResponseWrapper(response);
        chain.doFilter(request, wrapper);

        if (wrapper.getStatus() != HttpServletResponse.SC_OK) {
            wrapper.copyBodyToResponse();
            return;
        }

        String html = wrapper.getCapturedBody();

        html = injectNoticeWriteButton(html);
        html = injectNoticeWritePanel(html, "true".equals(request.getParameter("saved")));

        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    private String injectNoticeWriteButton(String html) {
        String target = """
                <span class="pill">총 <span""";

        String replacement = """
                <button type="button"
                        class="button"
                        onclick="showNoticeWritePanel()"
                        style="margin-left: auto; margin-right: 10px; border: 0; cursor: pointer;">
                    공지사항 등록
                </button>
                <span class="pill">총 <span""";

        return html.replace(target, replacement);
    }

    private String injectNoticeWritePanel(String html, boolean saved) {
        String target = "</main>";

        String successBox = saved
                ? """
                <div class="pill" style="display: inline-flex; width: fit-content; margin-bottom: 4px;">
                    공지사항이 등록되었습니다.
                </div>
                """
                : "";

        String panel = """
                <section id="notice-write-panel"
                         class="table-panel"
                         style="display: none;">
                    <div class="table-head">
                        <div>
                            <h3>공지사항 등록</h3>
                            <p>청년홈즈 사용자에게 보여줄 공지사항을 등록합니다.</p>
                        </div>
                        <button type="button"
                                class="button"
                                onclick="showNoticeListPanel()"
                                style="border: 0; cursor: pointer;">
                            목록으로
                        </button>
                    </div>

                    <div style="padding: 24px;">
                        __SUCCESS_BOX__

                        <form method="post" action="/admin/notice/write" style="display: grid; gap: 18px;">
                            <div style="display: grid; grid-template-columns: minmax(0, 1fr) 210px; gap: 14px; align-items: end;">
                                <div style="display: grid; gap: 8px;">
                                    <label style="font-weight: 800; color: #334155;">분류</label>
                                    <select name="category"
                                            style="height: 48px; width: 100%; border: 1px solid var(--line); border-radius: 14px; padding: 0 14px; font-size: 14px;">
                                        <option value="운영자 안내">운영자 안내</option>
                                        <option value="정책 변경">정책 변경</option>
                                        <option value="점검">점검</option>
                                        <option value="업데이트">업데이트</option>
                                        <option value="약관 변경">약관 변경</option>
                                    </select>
                                </div>

                                <label style="height: 48px; display: flex; align-items: center; justify-content: center; gap: 10px; padding: 0 14px; border: 1px solid var(--line); border-radius: 14px; background: #f8fbff; font-weight: 800; color: #334155; white-space: nowrap;">
                                    <input type="hidden" name="important" value="false">
                                    <input type="checkbox" name="important" value="true">
                                    중요 공지 표시
                                </label>
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">제목</label>
                                <input type="text"
                                       name="title"
                                       maxlength="200"
                                       placeholder="공지사항 제목을 입력하세요."
                                       required
                                       style="height: 48px; border: 1px solid var(--line); border-radius: 14px; padding: 0 14px; font-size: 14px;">
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">요약</label>
                                <input type="text"
                                       name="summary"
                                       maxlength="50"
                                       placeholder="목록에 표시될 짧은 설명을 입력하세요."
                                       required
                                       style="height: 48px; border: 1px solid var(--line); border-radius: 14px; padding: 0 14px; font-size: 14px;">
                                <small style="color: var(--muted); font-size: 13px;"></small>
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">내용</label>
                                <textarea name="content"
                                          placeholder="공지사항 상세 내용을 입력하세요."
                                          required
                                          style="min-height: 220px; border: 1px solid var(--line); border-radius: 14px; padding: 14px; resize: vertical; line-height: 1.7; font-size: 14px;"></textarea>
                            </div>

                            <div style="display: flex; justify-content: flex-end; gap: 10px; padding-top: 8px;">
                                <button type="button"
                                        onclick="showNoticeListPanel()"
                                        style="height: 46px; padding: 0 18px; border-radius: 14px; border: 1px solid var(--line); background: white; color: #475569; font-weight: 800; cursor: pointer;">
                                    취소
                                </button>

                                <button type="submit"
                                        class="button"
                                        style="height: 46px; border: 0; cursor: pointer;">
                                    등록하기
                                </button>
                            </div>
                        </form>
                    </div>
                </section>

                <script>
                    function showNoticeWritePanel() {
                        const panels = document.querySelectorAll('section.table-panel');
                        const listPanel = panels[0];
                        const writePanel = document.getElementById('notice-write-panel');

                        if (listPanel) {
                            listPanel.style.display = 'none';
                        }

                        if (writePanel) {
                            writePanel.style.display = 'block';
                        }

                        const title = document.querySelector('.hero h2');
                        const desc = document.querySelector('.hero p');
                        const badge = document.querySelector('.eyebrow');

                        if (badge) {
                            badge.textContent = '공지사항 등록';
                        }

                        if (title) {
                            title.textContent = '공지사항 등록';
                        }

                        if (desc) {
                            desc.textContent = '서비스 공지사항을 새로 등록합니다.';
                        }
                    }

                    function showNoticeListPanel() {
                        const panels = document.querySelectorAll('section.table-panel');
                        const listPanel = panels[0];
                        const writePanel = document.getElementById('notice-write-panel');

                        if (listPanel) {
                            listPanel.style.display = 'block';
                        }

                        if (writePanel) {
                            writePanel.style.display = 'none';
                        }

                        const title = document.querySelector('.hero h2');
                        const desc = document.querySelector('.hero p');
                        const badge = document.querySelector('.eyebrow');

                        if (badge) {
                            badge.textContent = '공지사항 관리';
                        }

                        if (title) {
                            title.textContent = '공지사항 관리';
                        }

                        if (desc) {
                            desc.textContent = '서비스 공지사항을 등록하고 확인합니다.';
                        }
                    }

                    window.addEventListener('DOMContentLoaded', function () {
                        const params = new URLSearchParams(window.location.search);
                        if (params.get('saved') === 'true') {
                            showNoticeWritePanel();
                        }
                    });
                </script>
                </main>
                """.replace("__SUCCESS_BOX__", successBox);

        return html.replace(target, panel);
    }

    private static class BufferedResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream capture = new ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;

        BufferedResponseWrapper(HttpServletResponse response) {
            super(response);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called.");
            }

            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }

                    @Override
                    public void write(int b) {
                        capture.write(b);
                    }
                };
            }

            return outputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called.");
            }

            if (writer == null) {
                writer = new PrintWriter(capture, true, StandardCharsets.UTF_8);
            }

            return writer;
        }

        String getCapturedBody() throws IOException {
            if (writer != null) {
                writer.flush();
            }

            if (outputStream != null) {
                outputStream.flush();
            }

            return capture.toString(StandardCharsets.UTF_8);
        }

        void copyBodyToResponse() throws IOException {
            byte[] bytes = capture.toByteArray();
            getResponse().getOutputStream().write(bytes);
        }
    }
}