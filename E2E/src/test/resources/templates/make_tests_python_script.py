from pathlib import Path
import subprocess
import shutil
import sys

# see doc at confluence
# https://students-team-w6om7azi.atlassian.net/wiki/spaces/J/pages/55574529/Automated+test+generation

BASE_DIR = Path(__file__).parent.resolve()

COMPILER_EXE = BASE_DIR / "javer-compiler" / "javer-compiler.exe"
VM_EXE = BASE_DIR / "javer-vm" / "javer-vm.exe"

TIMEOUT_SECONDS = 30

EXPECTED_TESTCASE_ROOT = r"E2E\src\test\resources\testcases"

PROTECTED_DIRS = {
    "javer-compiler",
    "javer-vm",
    "__pycache__"
}


def cleanup_old_test_directories():
    for item in BASE_DIR.iterdir():
        if not item.is_dir():
            continue

        if item.name in PROTECTED_DIRS:
            continue

        restore_input_files(item)
        shutil.rmtree(item)
        print(f"Deleted old test directory: {item.name}")


def restore_input_files(root_dir):
    for input_file in root_dir.rglob("input.javer"):
        relative_parent = input_file.parent.relative_to(BASE_DIR)
        restored_name = "_".join(relative_parent.parts) + ".javer"
        restored_file = BASE_DIR / restored_name

        if not restored_file.exists():
            shutil.move(str(input_file), str(restored_file))


def run_program(command, stdout_file, stderr_file):
    result = subprocess.run(
        command,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=TIMEOUT_SECONDS
    )

    stdout_file.write_text(result.stdout, encoding="utf-8")
    stderr_file.write_text(result.stderr, encoding="utf-8")

    return result.returncode


def normalize_paths_in_file(file_path, real_input_file, expected_path):
    if not file_path.exists():
        return

    content = file_path.read_text(encoding="utf-8", errors="replace")

    real_windows_path = str(real_input_file)
    real_posix_path = real_input_file.as_posix()

    content = content.replace(real_windows_path, expected_path)
    content = content.replace(real_posix_path, expected_path)

    file_path.write_text(content, encoding="utf-8")


def delete_compiler_outputs(test_dir):
    for file in test_dir.iterdir():
        if file.name.startswith("output"):
            file.unlink()


def main():
    cleanup_old_test_directories()

    if not COMPILER_EXE.exists():
        print(f"Compiler not found: {COMPILER_EXE}", file=sys.stderr)
        sys.exit(1)

    if not VM_EXE.exists():
        print(f"VM not found: {VM_EXE}", file=sys.stderr)
        sys.exit(1)

    javer_files = list(BASE_DIR.glob("*.javer"))

    for javer_file in javer_files:
        parts = javer_file.stem.split("_")

        test_dir = BASE_DIR.joinpath(*parts)
        test_dir.mkdir(parents=True, exist_ok=True)

        input_file = test_dir / "input.javer"
        shutil.move(str(javer_file), str(input_file))

        output_base = test_dir / "output"
        jbc_file = test_dir / "output.jbc"
        expected_jbc_file = test_dir / "expected_output.jbc"

        expected_path = EXPECTED_TESTCASE_ROOT + "/" + "/".join(parts) + "/input.javer"

        compiler_stdout = test_dir / "expected.compiler.stdout"
        compiler_stderr = test_dir / "expected.compiler.stderr"

        vm_stdout = test_dir / "expected.vm.stdout"
        vm_stderr = test_dir / "expected.vm.stderr"

        print(f"Generating test case: {'/'.join(parts)}")

        compiler_return_code = run_program(
            [
                str(COMPILER_EXE),
                "-i",
                str(input_file),
                "-o",
                str(output_base)
            ],
            compiler_stdout,
            compiler_stderr
        )

        normalize_paths_in_file(
            compiler_stderr,
            input_file,
            expected_path
        )

        if compiler_return_code != 0:
            print(f"  Compiler returned exit code {compiler_return_code}")

        if jbc_file.exists():
            vm_return_code = run_program(
                [
                    str(VM_EXE),
                    str(jbc_file)
                ],
                vm_stdout,
                vm_stderr
            )

            normalize_paths_in_file(
                vm_stderr,
                input_file,
                expected_path
            )

            if vm_return_code != 0:
                print(f"  VM returned exit code {vm_return_code}")

            shutil.move(str(jbc_file), str(expected_jbc_file))

            delete_compiler_outputs(test_dir)

        else:
            print("  No .jbc file generated, VM was not executed")

    print("Done.")


if __name__ == "__main__":
    main()