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
        html = injectNoticeWritePanel(
                html,
                "true".equals(request.getParameter("saved")),
                "true".equals(request.getParameter("updated")),
                "true".equals(request.getParameter("deleted"))
        );

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

    private String injectNoticeWritePanel(
            String html,
            boolean saved,
            boolean updated,
            boolean deleted
    ) {
        String target = "</main>";

        String successBox = "";

        if (saved) {
            successBox = """
                    <div class="pill" style="display: inline-flex; width: fit-content; margin-bottom: 4px;">
                        공지사항이 등록되었습니다.
                    </div>
                    """;
        }

        if (updated) {
            successBox = """
                    <div class="pill" style="display: inline-flex; width: fit-content; margin-bottom: 4px;">
                        공지사항이 수정되었습니다.
                    </div>
                    """;
        }

        if (deleted) {
            successBox = """
                    <div class="pill" style="display: inline-flex; width: fit-content; margin-bottom: 4px;">
                        공지사항이 삭제되었습니다.
                    </div>
                    """;
        }

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

                <section id="notice-edit-panel"
                         class="table-panel"
                         style="display: none;">
                    <div class="table-head">
                        <div>
                            <h3>공지사항 수정</h3>
                            <p>선택한 공지사항 내용을 수정합니다.</p>
                        </div>
                        <button type="button"
                                class="button"
                                onclick="showNoticeListPanel()"
                                style="border: 0; cursor: pointer;">
                            목록으로
                        </button>
                    </div>

                    <div style="padding: 24px;">
                        <form id="notice-edit-form" method="post" action="" style="display: grid; gap: 18px;">
                            <div style="display: grid; grid-template-columns: minmax(0, 1fr) 210px; gap: 14px; align-items: end;">
                                <div style="display: grid; gap: 8px;">
                                    <label style="font-weight: 800; color: #334155;">분류</label>
                                    <select id="notice-edit-category"
                                            name="category"
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
                                    <input id="notice-edit-important" type="checkbox" name="important" value="true">
                                    중요 공지 표시
                                </label>
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">제목</label>
                                <input id="notice-edit-title"
                                       type="text"
                                       name="title"
                                       maxlength="200"
                                       required
                                       style="height: 48px; border: 1px solid var(--line); border-radius: 14px; padding: 0 14px; font-size: 14px;">
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">요약</label>
                                <input id="notice-edit-summary"
                                       type="text"
                                       name="summary"
                                       maxlength="50"
                                       required
                                       style="height: 48px; border: 1px solid var(--line); border-radius: 14px; padding: 0 14px; font-size: 14px;">
                            </div>

                            <div style="display: grid; gap: 8px;">
                                <label style="font-weight: 800; color: #334155;">내용</label>
                                <textarea id="notice-edit-content"
                                          name="content"
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
                                    수정하기
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
                        const editPanel = document.getElementById('notice-edit-panel');

                        if (listPanel) {
                            listPanel.style.display = 'none';
                        }

                        if (editPanel) {
                            editPanel.style.display = 'none';
                        }

                        if (writePanel) {
                            writePanel.style.display = 'block';
                        }

                        updateNoticeHero('공지사항 등록', '공지사항 등록', '서비스 공지사항을 새로 등록합니다.');
                    }

                    function showNoticeEditPanel(noticeId, category, title, summary, content, important) {
                        const panels = document.querySelectorAll('section.table-panel');
                        const listPanel = panels[0];
                        const writePanel = document.getElementById('notice-write-panel');
                        const editPanel = document.getElementById('notice-edit-panel');
                        const form = document.getElementById('notice-edit-form');

                        if (listPanel) {
                            listPanel.style.display = 'none';
                        }

                        if (writePanel) {
                            writePanel.style.display = 'none';
                        }

                        if (editPanel) {
                            editPanel.style.display = 'block';
                        }

                        if (form) {
                            form.action = '/admin/notice/' + noticeId + '/edit';
                        }

                        document.getElementById('notice-edit-category').value = category || '운영자 안내';
                        document.getElementById('notice-edit-title').value = title || '';
                        document.getElementById('notice-edit-summary').value = summary || '';
                        document.getElementById('notice-edit-content').value = content || '';
                        document.getElementById('notice-edit-important').checked = important === true || important === 'true';

                        updateNoticeHero('공지사항 수정', '공지사항 수정', '선택한 공지사항 내용을 수정합니다.');
                    }

                    function showNoticeListPanel() {
                        const panels = document.querySelectorAll('section.table-panel');
                        const listPanel = panels[0];
                        const writePanel = document.getElementById('notice-write-panel');
                        const editPanel = document.getElementById('notice-edit-panel');

                        if (listPanel) {
                            listPanel.style.display = 'block';
                        }

                        if (writePanel) {
                            writePanel.style.display = 'none';
                        }

                        if (editPanel) {
                            editPanel.style.display = 'none';
                        }

                        updateNoticeHero('공지사항 관리', '공지사항 관리', '서비스 공지사항을 등록하고 확인합니다.');
                    }

                    function updateNoticeHero(badgeText, titleText, descText) {
                        const title = document.querySelector('.hero h2');
                        const desc = document.querySelector('.hero p');
                        const badge = document.querySelector('.eyebrow');

                        if (badge) {
                            badge.textContent = badgeText;
                        }

                        if (title) {
                            title.textContent = titleText;
                        }

                        if (desc) {
                            desc.textContent = descText;
                        }
                    }

                    function attachNoticeAdminActions() {
                        const listPanel = document.querySelector('section.table-panel');

                        if (!listPanel) {
                            console.log('공지사항 목록 패널을 찾지 못했습니다.');
                            return;
                        }

                        const rows = Array.from(listPanel.querySelectorAll('tbody tr'));

                        if (rows.length === 0) {
                            console.log('공지사항 행을 찾지 못했습니다.');
                            return;
                        }

                        fetch('/admin/notice/list', {
                            credentials: 'same-origin'
                        })
                            .then(function (response) {
                                if (!response.ok) {
                                    throw new Error('공지사항 목록 조회 실패');
                                }

                                return response.json();
                            })
                            .then(function (notices) {
                                rows.forEach(function (row) {
                                    if (row.querySelector('.notice-admin-actions')) {
                                        return;
                                    }

                                    const rowText = row.innerText || '';

                                    const matchedNotice = notices.find(function (notice) {
                                        const title = notice.title || '';
                                        const summary = notice.summary || '';

                                        return (
                                            (title && rowText.includes(title)) ||
                                            (summary && rowText.includes(summary))
                                        );
                                    });

                                    if (!matchedNotice) {
                                        console.log('매칭 실패 행:', rowText);
                                        return;
                                    }

                                    const noticeId = matchedNotice.noticeId;

                                    const actionCell = document.createElement('td');
                                    actionCell.className = 'notice-admin-actions';
                                    actionCell.style.whiteSpace = 'nowrap';
                                    actionCell.style.textAlign = 'right';
                                    actionCell.style.paddingLeft = '12px';

                                    const editButton = document.createElement('button');
                                    editButton.type = 'button';
                                    editButton.textContent = '수정';
                                    editButton.style.height = '34px';
                                    editButton.style.padding = '0 12px';
                                    editButton.style.borderRadius = '10px';
                                    editButton.style.border = '1px solid var(--line)';
                                    editButton.style.background = 'white';
                                    editButton.style.color = '#475569';
                                    editButton.style.fontWeight = '800';
                                    editButton.style.cursor = 'pointer';
                                    editButton.style.marginRight = '6px';

                                    editButton.addEventListener('click', function () {
                                        showNoticeEditPanel(
                                            matchedNotice.noticeId,
                                            matchedNotice.category,
                                            matchedNotice.title,
                                            matchedNotice.summary,
                                            matchedNotice.content,
                                            matchedNotice.important
                                        );
                                    });

                                    const deleteForm = document.createElement('form');
                                    deleteForm.method = 'post';
                                    deleteForm.action = '/admin/notice/' + noticeId + '/delete';
                                    deleteForm.style.display = 'inline';

                                    deleteForm.addEventListener('submit', function (event) {
                                        const confirmed = confirm('정말 이 공지사항을 삭제할까요?');

                                        if (!confirmed) {
                                            event.preventDefault();
                                        }
                                    });

                                    const deleteButton = document.createElement('button');
                                    deleteButton.type = 'submit';
                                    deleteButton.textContent = '삭제';
                                    deleteButton.style.height = '34px';
                                    deleteButton.style.padding = '0 12px';
                                    deleteButton.style.borderRadius = '10px';
                                    deleteButton.style.border = '0';
                                    deleteButton.style.background = '#ef4444';
                                    deleteButton.style.color = 'white';
                                    deleteButton.style.fontWeight = '800';
                                    deleteButton.style.cursor = 'pointer';

                                    deleteForm.appendChild(deleteButton);
                                    actionCell.appendChild(editButton);
                                    actionCell.appendChild(deleteForm);
                                    row.appendChild(actionCell);
                                });
                            })
                            .catch(function (error) {
                                console.error('공지사항 수정/삭제 버튼 추가 실패:', error);
                            });
                    }

                    window.addEventListener('DOMContentLoaded', function () {
                        attachNoticeAdminActions();

                        const params = new URLSearchParams(window.location.search);

                        if (params.get('saved') === 'true') {
                            showNoticeWritePanel();
                        }

                        if (params.get('updated') === 'true' || params.get('deleted') === 'true') {
                            showNoticeListPanel();
                        }
                    });
                </script>
                </main>
                """
                .replace("__SUCCESS_BOX__", successBox);

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